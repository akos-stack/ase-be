package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.testutil.MockUtil.ERROR_CODE;
import static com.bloxico.ase.userservice.web.api.UserPasswordApi.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

// Because RestAssured executes in another transaction
@Transactional(propagation = NOT_SUPPORTED)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UserPasswordApiTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Test
    public void initForgotPasswordProcedure_200_ok() {
        var email = mockUtil.savedUser().getEmail();
        given()
                .contentType(JSON)
                .body(new ForgotPasswordRequest(email))
                .when()
                .post(API_URL + PASSWORD_FORGOT_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void initForgotPasswordProcedure_404_userNotFound() {
        var email = "userNotFound@mail.com";
        given()
                .contentType(JSON)
                .body(new ForgotPasswordRequest(email))
                .when()
                .post(API_URL + PASSWORD_FORGOT_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void resendPasswordResetToken_200_ok() {
        var email = mockUtil.savedAdmin().getEmail();
        mockUtil.doForgotPasswordRequest(email);
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(email))
                .when()
                .post(API_URL + PASSWORD_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void resendPasswordResetToken_404_userNotFound() {
        var email = "userNotFound@mail.com";
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(email))
                .when()
                .post(API_URL + PASSWORD_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void resendPasswordResetToken_404_tokenNotFound() {
        var email = mockUtil.savedAdmin().getEmail();
        given()
                .contentType(JSON)
                .body(new ResendTokenRequest(email))
                .when()
                .post(API_URL + PASSWORD_TOKEN_RESEND_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void updateForgottenPassword_200_ok() {
        var email = mockUtil.savedAdmin().getEmail();
        var token = mockUtil.doForgotPasswordRequest(email);
        var password = "Password9!";
        given()
                .contentType(JSON)
                .body(new ForgottenPasswordUpdateRequest(email, token, password))
                .when()
                .post(API_URL + PASSWORD_UPDATE_FORGOTTEN_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateForgottenPassword_404_userNotFound() {
        var email = "userNotFound@mail.com";
        var token = "doesNotMatter";
        var password = "Password9!";
        given()
                .contentType(JSON)
                .body(new ForgottenPasswordUpdateRequest(email, token, password))
                .when()
                .post(API_URL + PASSWORD_UPDATE_FORGOTTEN_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.User.USER_NOT_FOUND.getCode()));
    }

    @Test
    public void updateForgottenPassword_404_tokenNotFound() {
        var email = mockUtil.savedAdmin().getEmail();
        var token = "doesNotMatter";
        var password = "Password9!";
        given()
                .contentType(JSON)
                .body(new ForgottenPasswordUpdateRequest(email, token, password))
                .when()
                .post(API_URL + PASSWORD_UPDATE_FORGOTTEN_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(404)
                .body(ERROR_CODE, is(ErrorCodes.Token.TOKEN_NOT_FOUND.getCode()));
    }

    @Test
    public void updateKnownPassword_200_ok() {
        var oldPassword = "Password1!";
        var newPassword = "Password2!";
        given()
                .header("Authorization", mockUtil.doAuthentication(oldPassword))
                .contentType(JSON)
                .body(new KnownPasswordUpdateRequest(oldPassword, newPassword))
                .when()
                .post(API_URL + PASSWORD_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void updateKnownPassword_400_oldPasswordMismatch() {
        var oldPassword = "Password123!";
        var newPassword = "Password2!";
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new KnownPasswordUpdateRequest(oldPassword, newPassword))
                .when()
                .post(API_URL + PASSWORD_UPDATE_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(400)
                .body(ERROR_CODE, is(ErrorCodes.User.OLD_PASSWORD_DOES_NOT_MATCH.getCode()));
    }

    @Test
    public void setNewPassword_200_ok() {
        given()
                .header("Authorization", mockUtil.doAuthentication())
                .contentType(JSON)
                .body(new SetPasswordRequest("Password123!"))
                .when()
                .post(API_URL + PASSWORD_SET_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(200);
    }

}
