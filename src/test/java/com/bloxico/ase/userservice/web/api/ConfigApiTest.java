package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilConfig;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.web.api.ConfigApi.CONFIG_SAVE;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
public class ConfigApiTest extends AbstractSpringTest {

    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private UtilConfig utilConfig;

    @Test
    @WithMockCustomUser(auth = true)
    public void saveConfig_200_ok() {
        var request = utilConfig.genSaveConfigRequest();
        var config = given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + CONFIG_SAVE)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SaveConfigResponse.class)
                .getConfig();
        assertNotNull(config);
        assertNotNull(config.getId());
        assertEquals(request.getType(), config.getType());
        assertEquals(request.getValue(), config.getValue());
    }

}
