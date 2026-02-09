package fish.focus.uvms.plugins.vms2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fish.focus.uvms.plugins.vms2.gen.model.Coordinates;
import fish.focus.uvms.plugins.vms2.gen.model.Reporter;
import fish.focus.uvms.plugins.vms2.gen.model.Vessel;
import fish.focus.uvms.plugins.vms2.gen.model.VesselPosition;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static org.hamcrest.Matchers.*;

@RunWith(Arquillian.class)
@RunAsClient
public class PositionsApiImplTest extends BuildDeployment {

    private static final String POSITIONS_URL = "http://localhost:28080/unionvms/vms2/rest/positions";
    private static final String RESULT_URL = "http://localhost:28080/unionvms/vms2/rest/test/result";

    @BeforeClass
    public static void setup() {
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, s) -> {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.findAndRegisterModules();
                            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                            return objectMapper;
                        }));
    }

    @Test
    @OperateOnDeployment("normal")
    public void givenEmptyPosition_whenPosting_thenBadRequestReturned() {
        var vesselPosition = new VesselPosition();

        given()
            .body(vesselPosition)
            .contentType("application/json")
        .when()
            .post(POSITIONS_URL)
        .then()
            .statusCode(400)
            .body("id", is(notNullValue()))
            .body("detail",
                    allOf(
                        containsString("coordinates"),
                        containsString("positionAt"),
                        containsString("reporter"),
                        containsString("vessel"),
                        containsString("messageId")
                )
            );
    }

    @Test
    @OperateOnDeployment("normal")
    public void givenValidPosition_whenPosting_thenReturnCreated() {
        var vesselPosition = getValidVesselPosition();

        given()
            .body(vesselPosition)
            .contentType("application/json")
        .when()
            .post(POSITIONS_URL)
        .then()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("code", is(201));
    }

    @Test
    @OperateOnDeployment("normal")
    public void givenValidPosition_whenPosting_thenReturnCreatedInmarsat() {
        var vesselPosition = getValidVesselPositionInmarsat();

        given()
            .body(vesselPosition)
            .contentType("application/json")
        .when()
            .post(POSITIONS_URL)
        .then()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("code", is(201));
    }

    private static VesselPosition getValidVesselPosition() {
        String messageId = UUID.randomUUID().toString();

        var cfr = "CFR411045665";
        var vessel = new Vessel(cfr);

        Double lat = 57.676840;
        Double lon = 11.375151;

        Coordinates coordinates =  new Coordinates(lat, lon);

        Instant positionAt =  Instant.now();

        Instant reportedAt = Instant.now();
        String externalSystem = "VMS";
        String source = "IRIDIUM";
        String reportType = "position";
        Reporter reporter = new Reporter(reportedAt, externalSystem, source, reportType);

        return new VesselPosition(messageId, vessel, coordinates, positionAt, reporter);
    }

    private static VesselPosition getValidVesselPositionInmarsat() {
        String messageId = UUID.randomUUID().toString();

        var cfr = "CFR167844536";
        var vessel = new Vessel(cfr);

        Double lat = 58.686840;
        Double lon = 12.395151;

        Coordinates coordinates =  new Coordinates(lat, lon);

        Instant positionAt =  Instant.now();

        Instant reportedAt = Instant.now();
        String externalSystem = "VMS";
        String source = "INMARSAT-C";
        String reportType = "position";
        Reporter reporter = new Reporter(reportedAt, externalSystem, source, reportType);

        return new VesselPosition(messageId, vessel, coordinates, positionAt, reporter);
    }
}
