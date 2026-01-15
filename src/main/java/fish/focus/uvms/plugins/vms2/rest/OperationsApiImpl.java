package fish.focus.uvms.plugins.vms2.rest;

import fish.focus.uvms.plugins.vms2.gen.api.OperationsApi;
import fish.focus.uvms.plugins.vms2.gen.model.HealthCheck;
import fish.focus.uvms.plugins.vms2.gen.model.MetadataResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;

@Path("")
@RequestScoped
public class OperationsApiImpl implements OperationsApi {

    @Inject
    @ConfigProperty(name = "apiName")
    private String apiName;

    @Inject
    @ConfigProperty(name = "apiVersion")
    private String apiVersion;

    @Inject
    @ConfigProperty(name = "apiReleasedAt")
    private String apiReleasedAt;

    @Inject
    @ConfigProperty(name = "apiDocumentation")
    private String apiDocumentation;

    @Inject
    @ConfigProperty(name = "apiStatus")
    private String apiStatus;

    @Override
    @SimplyTimed
    public Response getHealthChecksReadiness() {
        HealthCheck healthCheck = new HealthCheck("Service is ready", "up");
        var response = Map.of("vms2", healthCheck);

        return Response.ok(response).build();
    }

    @Override
    @SimplyTimed
    public Response getMetadata() {
        Instant apiReleasedAtInstant = Instant.parse(apiReleasedAt);
        var response = new MetadataResponse()
                .apiName(apiName)
                .apiVersion(apiVersion)
                .apiReleasedAt(apiReleasedAtInstant)
                .apiDocumentation(apiDocumentation)
                .apiStatus(apiStatus);

        return Response.ok(response).build();
    }
}
