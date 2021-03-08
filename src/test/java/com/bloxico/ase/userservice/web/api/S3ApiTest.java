package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilAuth;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.bloxico.ase.testutil.Util.genFileBytes;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.web.api.S3Api.S3_VALIDATE_FILES;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
public class S3ApiTest extends AbstractSpringTestWithAWS{

    @Autowired
    private UtilAuth utilAuth;

    @Test
    public void validateFiles_200_ok_FileCategory_CV() {
        var registration = utilAuth.doConfirmedRegistration();
        var textBytes_CV = genFileBytes(CV);
        var textBytes2_CV = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileCategory", "CV")
                .multiPart("files",  "testCV.txt", textBytes_CV)
                .multiPart("files",  "test2CV.txt", textBytes2_CV)
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void validateFiles_200_ok_FileCategory_IMAGE() {
        var registration = utilAuth.doConfirmedRegistration();
        var imageBytes_JPG = genFileBytes(IMAGE);
        var imageBytes2_JPG = genFileBytes(IMAGE);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  "testIMAGE.jpg", imageBytes_JPG)
                .multiPart("files",  "test2IMAGE.jpg", imageBytes2_JPG)
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void validateFiles_400_file_type_not_supported_for_category() {
        var registration = utilAuth.doConfirmedRegistration();
        var imageBytes_JPG = genFileBytes(IMAGE);
        var textBytes2_CV = genFileBytes(CV);
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  "testIMAGE.jpg", imageBytes_JPG)
                .multiPart("files",  "test2CV.txt", textBytes2_CV)
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .body()
                .as(ArrayList.class);
        var invalidFiles = new ArrayList<String>();
        invalidFiles.add("test2CV.txt");
        assertNotNull(response);
        assertEquals(response, invalidFiles);
    }

    @Test
    public void validateFiles_400_wrong_file_category() {
        var registration = utilAuth.doConfirmedRegistration();
        var imageBytes_JPG = genFileBytes(IMAGE);
        var imageBytes2_JPG = genFileBytes(IMAGE);
        var response = given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileCategory", "CV")
                .multiPart("files",  "testIMAGE.jpg", imageBytes_JPG)
                .multiPart("files",  "test2IMAGE.png", imageBytes2_JPG)
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .body()
                .as(ArrayList.class);
        var invalidFiles = new ArrayList<String>();
        invalidFiles.add("testIMAGE.jpg");
        invalidFiles.add("test2IMAGE.png");
        assertNotNull(response);
        assertEquals(response, invalidFiles);
    }

    @Test
    public void validateFiles_400_one_file() {
        var registration = utilAuth.doConfirmedRegistration();
        var imageBytes_JPG = genFileBytes(IMAGE);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileCategory", "IMAGE")
                .multiPart("files",  "testIMAGE.jpg", imageBytes_JPG)
                .when()
                .post(API_URL + S3_VALIDATE_FILES)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
