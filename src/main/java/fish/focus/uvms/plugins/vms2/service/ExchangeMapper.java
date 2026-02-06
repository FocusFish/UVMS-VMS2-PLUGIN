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

import java.util.Date;

@Stateless
public class ExchangeMapper {
    @Inject
    StartupBean startupBean;

    public SetReportMovementType getSetReportMovementType(VesselPosition vesselPosition) {
        MovementBaseType movement = createMovementBaseType(vesselPosition);

        return getMovementReport(movement);
    }

    private MovementBaseType createMovementBaseType(VesselPosition vesselPosition) {
        
        MovementBaseType movement = new MovementBaseType();
        
        movement.setAssetId(getAssetId(vesselPosition));
        movement.setPosition(getMovementPoint(vesselPosition));
        movement.setComChannelType(MovementComChannelType.MOBILE_TERMINAL);
        movement.setMovementType(MovementTypeType.POS);
        movement.setPositionTime(Date.from(vesselPosition.getPositionAt()));
        movement.setReportedCourse(vesselPosition.getCourseOverGround());
        movement.setReportedSpeed(vesselPosition.getSpeedOverGround());
         
        if ( vesselPosition.getReporter() != null
            && vesselPosition.getReporter().getSource() != null
            && "INMARSAT-C".equalsIgnoreCase(vesselPosition.getReporter().getSource()) ) {

            movement.setSource(MovementSourceType.INMARSAT_C);

        } else {
            movement.setSource(MovementSourceType.IRIDIUM);
        }

        
        movement.setLesReportTime(Date.from(vesselPosition.getReporter().getReportedAt()));
        movement.setPositionTime(Date.from(vesselPosition.getPositionAt()));
        
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

    private SetReportMovementType getMovementReport(MovementBaseType movement) {
        SetReportMovementType report = new SetReportMovementType();
        report.setTimestamp(new Date());
        report.setPluginName(startupBean.getRegisterClassName());
        report.setPluginType(PluginType.OTHER);
        report.setMovement(movement);
        return report;
    }

}
