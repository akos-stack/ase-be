package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilConfig;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import com.bloxico.ase.userservice.web.model.config.SearchConfigResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.ERROR_CODE;
import static com.bloxico.ase.userservice.entity.config.Config.Type.QUOTATION_PACKAGE_MIN_EVALUATIONS;
import static com.bloxico.ase.userservice.web.api.ConfigApi.CONFIG_SAVE;
import static com.bloxico.ase.userservice.web.api.ConfigApi.CONFIG_SEARCH;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Config.CONFIG_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
public class ConfigApiTest extends AbstractSpringTest {

    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private UtilConfig utilConfig;

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void searchConfig_200_ok() {
        var config = utilConfig.savedQuotationPackageMinEvaluatorsConfigDto();
        var foundConfig = given()
                .header("Authorization", utilSecurityContext.getToken())
                .queryParam("type", config.getType().toString())
                .when()
                .get(API_URL + CONFIG_SEARCH)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(SearchConfigResponse.class)
                .getConfig();
        assertNotNull(foundConfig);
        assertEquals(config.getId(), foundConfig.getId());
        assertEquals(config.getType(), foundConfig.getType());
        assertEquals(config.getValue(), foundConfig.getValue());
    }

    @Test
    @WithMockCustomUser(role = Role.USER, auth = true)
    public void searchConfig_404_notFound() {
        var config = utilConfig.savedQuotationPackageMinEvaluatorsConfigDto();
        utilConfig.deleteConfigById(config.getId());
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .queryParam("type", config.getType().toString())
                .when()
                .get(API_URL + CONFIG_SEARCH)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(CONFIG_NOT_FOUND.getCode()));
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveConfig_400_invalidValue() {
        var request = utilConfig.savedQuotationPackageMinEvaluatorsConfigDto(-1);
        given()
                .header("Authorization", utilSecurityContext.getToken())
                .contentType(JSON)
                .body(request)
                .when()
                .post(API_URL + CONFIG_SAVE)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    @WithMockCustomUser(auth = true)
    public void saveConfig_200_ok() {
        var request = utilConfig.genSaveConfigRequest(QUOTATION_PACKAGE_MIN_EVALUATIONS, 10);
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
        assertEquals(request.getValue().toString(), config.getValue());
    }

}
