package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.facade.impl.UserPasswordFacadeImpl;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genEmail;
import static com.bloxico.ase.testutil.Util.genPassword;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_CONFIRM_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;
import static java.util.Objects.requireNonNull;

@Component
public class UtilAuth {

    @Value("${api.url}")
    private String API_URL;

    @Value("${oauth2.client.id}")
    private String OAUTH2_CLIENT_ID;

    @Value("${oauth2.secret}")
    private String OAUTH2_SECRET;

    @Autowired private UtilUser utilUser;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private UserServiceImpl userService;
    @Autowired private UserPasswordFacadeImpl userPasswordFacade;

    public RegistrationRequest genRegistrationRequest() {
        var email = genEmail();
        var password = genPassword();
        return new RegistrationRequest(email, email, password, password, Set.of());
    }

    public RegistrationRequest genRegistrationRequestPasswordMismatch() {
        var email = genEmail();
        return new RegistrationRequest(email, email, genPassword(), genPassword(), Set.of());
    }

    public RegistrationRequest genRegistrationRequestWithAspirations(String... aspirations) {
        var email = genEmail();
        var password = genPassword();
        return new RegistrationRequest(email, email, password, password, Set.of(aspirations));
    }

    @lombok.Value
    public static class Registration {
        long id;
        String email, username, password, token;
    }

    public Registration doRegistration() {
        var email = genEmail();
        var password = genPassword();
        var token = given()
                .contentType(JSON)
                .body(new RegistrationRequest(email, email, password, password, Set.of()))
                .post(API_URL + REGISTRATION_ENDPOINT)
                .body()
                .as(RegistrationResponse.class)
                .getTokenValue();
        var id = userService.findUserByEmail(email).getId();
        return new Registration(id, email, email, password, token);
    }

    public void doConfirmation(String token) {
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(token))
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT);
    }

    public Registration doConfirmedRegistration() {
        var registration = doRegistration();
        var token = registration.getToken();
        doConfirmation(token);
        return registration;
    }

    public String doAuthentication() {
        return doAuthentication(doConfirmedRegistration());
    }

    public String doAuthentication(String password) {
        var email = utilUser.savedUserWithPassword(password).getEmail();
        return doAuthentication(email, password);
    }

    public String doAuthentication(Registration registration) {
        return doAuthentication(
                registration.getEmail(),
                registration.getPassword());
    }

    public String doAdminAuthentication() {
        var password = genPassword();
        var email = utilUser.savedAdmin(password).getEmail();
        return doAuthentication(email, password);
    }

    public String doAuthentication(String email, String password) {
        return "Bearer " + requireNonNull(given()
                .contentType(URLENC)
                .accept(JSON)
                .formParams(
                        "username", email,
                        "password", password,
                        "grant_type", "password",
                        "scope", "access_profile")
                .auth()
                .preemptive()
                .basic(OAUTH2_CLIENT_ID, OAUTH2_SECRET)
                .when()
                .post(API_URL + "/oauth/token")
                .body()
                .jsonPath()
                .getString("access_token"));
    }

    public Response doAuthenticationRequest(Registration registration) {
        return given()
                .contentType(URLENC)
                .accept(JSON)
                .formParams(
                        "username", registration.getEmail(),
                        "password", registration.getPassword(),
                        "grant_type", "password",
                        "scope", "access_profile")
                .auth()
                .preemptive()
                .basic(OAUTH2_CLIENT_ID, OAUTH2_SECRET)
                .when()
                .post(API_URL + "/oauth/token");
    }

    public String doForgotPasswordRequest(String email) {
        var request = new ForgotPasswordRequest(email);
        userPasswordFacade.handleForgotPasswordRequest(request);
        var userId = userService.findUserByEmail(email).getId();
        return tokenRepository
                .findByTypeAndUserId(PASSWORD_RESET, userId)
                .orElseThrow()
                .getValue();
    }

}
