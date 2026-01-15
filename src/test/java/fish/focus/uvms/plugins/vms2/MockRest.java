package fish.focus.uvms.plugins.vms2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

@Path("")
public class MockRest {
    @GET
    @Path("/test/result")
    @Produces({ "application/json", "application/problem+json" })
    public Map<String, Boolean> readiness() {
        return Map.of("result", DataHolder.HAS_RECEIVED_MOVEMENT.get());
    }

}
