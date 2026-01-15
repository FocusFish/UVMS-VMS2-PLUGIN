package fish.focus.uvms.plugins.vms2.service;

import fish.focus.schema.exchange.movement.asset.v1.AssetId;
import fish.focus.schema.exchange.movement.asset.v1.AssetIdList;
import fish.focus.schema.exchange.movement.asset.v1.AssetIdType;
import fish.focus.schema.exchange.movement.asset.v1.AssetType;
import fish.focus.schema.exchange.movement.v1.*;
import fish.focus.schema.exchange.plugin.types.v1.PluginType;
import fish.focus.uvms.plugins.vms2.gen.model.VesselPosition;
import fish.focus.uvms.plugins.vms2.StartupBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@Stateless
public class ExchangeMapper {
    @Inject
    StartupBean startupBean;

    public SetReportMovementType getSetReportMovementType(VesselPosition vesselPosition) {
        MovementBaseType movement = createMovementBaseType(vesselPosition);

        return createSetReportMovementType(vesselPosition.toString().getBytes(UTF_8), movement);
    }

    private MovementBaseType createMovementBaseType(VesselPosition vesselPosition) {
        MovementBaseType movement = new MovementBaseType();

        AssetId assetId = getAssetId(vesselPosition);
        movement.setAssetId(assetId);

        MovementPoint movementPoint = getMovementPoint(vesselPosition);
        movement.setPosition(movementPoint);

        movement.setComChannelType(MovementComChannelType.MOBILE_TERMINAL);
        movement.setMovementType(MovementTypeType.POS);
        movement.setPositionTime(Date.from(vesselPosition.getPositionAt()));
        movement.setReportedCourse(vesselPosition.getCourseOverGround());
        movement.setReportedSpeed(vesselPosition.getSpeedOverGround());

        movement.setSource(MovementSourceType.IRIDIUM);
        // options for status?
//        movement.setStatus(vesselPosition.getMessageId());
//        movement.setStatus(vesselPosition.getReporter().getQuality().getPositionAccuracy() + "-" + vesselPosition.getReporter().getQuality().getSourceReliability());

        movement.setLesReportTime(Date.from(vesselPosition.getReporter().getReportedAt()));

        return movement;
    }

    private AssetId getAssetId(VesselPosition vesselPosition) {
        AssetIdList assetIdEntry = new AssetIdList();
        assetIdEntry.setIdType(AssetIdType.CFR);
        assetIdEntry.setValue(vesselPosition.getVessel().getCfr());

        AssetId assetId = new AssetId();
        assetId.setAssetType(AssetType.VESSEL);
        assetId.getAssetIdList().add(assetIdEntry);

        return assetId;
    }

    private MovementPoint getMovementPoint(VesselPosition vesselPosition) {
        MovementPoint movementPoint = new MovementPoint();
        movementPoint.setAltitude(0.0);
        movementPoint.setLatitude(vesselPosition.getCoordinates().getLatitude());
        movementPoint.setLongitude(vesselPosition.getCoordinates().getLongitude());
        return movementPoint;
    }

    private SetReportMovementType createSetReportMovementType(byte[] messageAsBytes, MovementBaseType movement) {
        SetReportMovementType reportType = new SetReportMovementType();

        reportType.setMovement(movement);
        reportType.setTimestamp(new Date());
        reportType.setPluginName(startupBean.getRegisterClassName() + "." + startupBean.getApplicationName());
        reportType.setPluginType(PluginType.SATELLITE_RECEIVER);
        reportType.setOriginalIncomingMessage(Base64.getEncoder().encodeToString(messageAsBytes));

        return reportType;
    }
}
