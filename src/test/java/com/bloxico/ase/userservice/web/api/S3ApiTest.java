package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilAuth;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.util.SupportedFileExtension;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import static com.bloxico.ase.userservice.web.api.S3Api.S3_DOWNLOAD;
import static com.bloxico.ase.userservice.web.api.S3Api.S3_DELETE;

@Transactional(propagation = NOT_SUPPORTED)
public class S3ApiTest extends AbstractSpringTestWithAWS{

    @Autowired private UtilAuth utilAuth;
    @Autowired private S3ServiceImpl s3Service;

    @Test
    public void downloadFile_ok(){
        var fileName = s3Service.uploadFile(IMAGE, genMultipartFile(IMAGE));
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileName", fileName)
                .when()
                .get(API_URL + S3_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    public void downloadFile_not_found(){
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileName", genUUID())
                .when()
                .get(API_URL + S3_DOWNLOAD)
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void deleteFile_ok(){
        var fileName = s3Service.uploadFile(IMAGE, genMultipartFile(SupportedFileExtension.png));
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileName", fileName)
                .when()
                .delete(API_URL + S3_DELETE)
                .then()
                .assertThat()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    public void deleteFile_not_found(){
        given()
                .header("Authorization", utilAuth.doAdminAuthentication())
                .formParam("fileName", genUUID())
                .when()
                .delete(API_URL + S3_DELETE)
                .then()
                .assertThat()
                .statusCode(400);
    }
}
