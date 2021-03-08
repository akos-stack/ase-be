package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilAuth;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static io.restassured.RestAssured.given;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import java.util.ArrayList;

import static com.bloxico.ase.testutil.Util.genFileBytes;
import static com.bloxico.ase.userservice.web.api.S3Api.S3_VALIDATE_FILES;

@Transactional(propagation = NOT_SUPPORTED)
public class S3ApiTest extends AbstractSpringTestWithAWS{

    @Autowired
    private UtilAuth utilAuth;

    @Test
    public void validateFiles_200_ok_FileCategory_CV() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "CV")
                .multiPart("files",genUUID() + ".txt", genFileBytes(CV))
                .multiPart("files",genUUID() + ".txt", genFileBytes(CV))
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void validateFiles_200_ok_FileCategory_IMAGE() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void validateFiles_400_file_type_not_supported_for_category() {
        var registration = utilAuth.doConfirmedRegistration();
        var textFile = genUUID() + ".txt";
        var responseAssumption = new ArrayList(){{add(textFile);}};
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .multiPart("files",  textFile, genFileBytes(CV))
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .body()
                .as(ArrayList.class);
        assertNotNull(response);
        assertEquals(response, responseAssumption);
    }

    @Test
    public void validateFiles_400_wrong_file_category() {
        var registration = utilAuth.doConfirmedRegistration();
        var imageFile = genUUID() + ".jpg";
        var imageFile2 = genUUID() + ".jpg";
        var responseAssumption = new ArrayList(){{add(imageFile); add(imageFile2);}};
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "CV")
                .multiPart("files",  imageFile, genFileBytes(IMAGE))
                .multiPart("files",  imageFile2, genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .body()
                .as(ArrayList.class);
        assertNotNull(response);
        assertEquals(response, responseAssumption);
    }

    @Test
    public void validateFiles_200_send_one_file() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  genUUID() + ".jpg", genFileBytes(IMAGE))
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
