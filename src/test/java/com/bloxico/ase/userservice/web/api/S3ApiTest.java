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

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static com.bloxico.ase.userservice.web.api.S3Api.S3_VALIDATE;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_EVALUATOR_SUBMIT;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_EXISTS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Transactional(propagation = NOT_SUPPORTED)
public class S3ApiTest extends AbstractSpringTestWithAWS {

    @Autowired
    private UtilAuth utilAuth;

    @Test
    public void validateFile_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "CV")
                .multiPart("file", genUUID() + ".txt", genFileBytes(CV))
                .when()
                .post(API_URL + S3_VALIDATE)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void validateFile_400_() {
        var registration = utilAuth.doConfirmedRegistration();
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .formParam("fileCategory", "null")
                .multiPart("file", genUUID() + ".txt", genFileBytes(CV))
                .when()
                .post(API_URL + S3_VALIDATE)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(USER_EXISTS.getCode()));
    }
    
    @Test
    public void downloadFile_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var textBytes_CV = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileName", "testCV.txt")
                .when()
                .post(API_URL + S3_VALIDATE)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteFile_200_ok() {
        var registration = utilAuth.doConfirmedRegistration();
        var textBytes_CV = genFileBytes(CV);
        given()
                .header("Authorization", utilAuth.doAuthentication(registration))
                .contentType("multipart/form-data")
                .formParam("fileName", "testCV.txt")
                .when()
                .post(API_URL + S3_VALIDATE)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
