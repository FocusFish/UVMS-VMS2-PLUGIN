package fish.focus.uvms.plugins.vms2;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Arquillian.class)
@RunAsClient
public class OperationsApiImplTest extends BuildDeployment {

    private static final String BASE_URL = "http://localhost:28080/unionvms/vms2/rest/";

    @Test
    @OperateOnDeployment("normal")
    public void givenUpAndRunning_whenRequestingStatus_thenReturnStatusUp() {
        var readinessUrl = BASE_URL + "operations/health-checks/readiness";

        when()
            .get(readinessUrl)
        .then()
            .body("vms2.state", equalTo("up"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void givenUpAndRunning_whenRequestingMetaData_thenApiNameIsSet() {
        var metaDataUrl = BASE_URL + "operations/metadata";

        when()
            .get(metaDataUrl)
        .then()
            .body("apiName", equalTo("vms2"));
    }
}
