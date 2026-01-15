package fish.focus.uvms.plugins.vms2.rest;

import fish.focus.uvms.plugins.vms2.exception.PositionException;
import fish.focus.uvms.plugins.vms2.gen.api.PositionsApi;
import fish.focus.uvms.plugins.vms2.gen.model.VesselPosition;
import fish.focus.uvms.plugins.vms2.gen.model.VesselPositionResponse;
import fish.focus.uvms.plugins.vms2.service.Exchange;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;

@Path("")
public class PositionsApiImpl implements PositionsApi {

    @Inject
    private Exchange exchange;

    @Override
    @Timed(name = "create_position_timer")
    public Response postVesselPosition(VesselPosition vesselPosition) {
        if (vesselPosition == null) {
            throw new PositionException("Input vessel position is null.");
        }

        exchange.save(vesselPosition);

        var response = new VesselPositionResponse()
                .code(CREATED.getStatusCode())
                .id(MDC.get("requestId"))
                .detail("Vessel Position Created");

        return Response
                .status(CREATED)
                .entity(response)
                .build();
    }
}
