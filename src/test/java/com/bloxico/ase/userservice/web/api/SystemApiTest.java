package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.UtilSystem.assertValidSystemConstants;
import static com.bloxico.ase.userservice.web.api.SystemApi.SYSTEM_CONSTANTS;
import static io.restassured.RestAssured.given;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
public class SystemApiTest extends AbstractSpringTest {

    @Test
    public void systemConstants_200_ok() {
        var constants = given()
                .when()
                .get(API_URL + SYSTEM_CONSTANTS)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SystemConstantsResponse.class)
                .getConstants();
        assertValidSystemConstants(constants);
    }

}
