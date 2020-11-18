package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static com.bloxico.ase.userservice.web.api.Temp.ASE;

@Api(value = "registration")
public interface UserRegistrationApi {

    String REGISTRATION_ENDPOINT               = ASE + "/user/registration";
    String REGISTRATION_CONFIRMATION_ENDPOINT  = ASE + "/user/registrationConfirm";
    String REGISTRATION_TOKEN_REFRESH_ENDPOINT = ASE + "/user/registrationTokenRefresh";
    String REGISTRATION_TOKEN_RESEND_ENDPOINT  = ASE + "/user/registrationTokenResend";

    String TOKEN_PARAM = "token";

    @PostMapping(
            value = REGISTRATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Creates disabled user in the database and sends verification token to provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Registration successfully done."),
            @ApiResponse(code = 400, message = "Values `password` and `match_password` do not match."),
            @ApiResponse(code = 409, message = "User already exists.")
    })
    ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request);

    @PostMapping(
            value = REGISTRATION_CONFIRMATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Accepts verification token provided by email and, if correct, enables user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Validation successful - user is now able to log in."),
            @ApiResponse(code = 404, message = "Could not find user associated with given email."),
            @ApiResponse(code = 404, message = "Token provided is either not found or expired."),
    })
    ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody TokenValidationRequest request);

    @ApiOperation(
            value = "Endpoint that has to be called if token that is being validated has expired. " +
                    "This will rarely happen since all expired tokens are being deleted within 24 hours " +
                    "but in case if user tries to validate token that is expired, this endpoint needs to be called " +
                    "in order to refresh the token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is refreshed and email has been sent with updated token value."),
            @ApiResponse(code = 404, message = "Token provided is not found."),
    })
    @GetMapping(value = REGISTRATION_TOKEN_REFRESH_ENDPOINT)
    ResponseEntity<Void> refreshVerificationToken(@ApiParam(value = "Expired token value", required = true)
                                                  @RequestParam(TOKEN_PARAM) String token);

    @PostMapping(
            value = REGISTRATION_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "In case the mail has not been sent with verification token, resend email using " +
                          "existing token in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 404, message = "Could not find user associated with given email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found, it probably expired."),
    })
    ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody ResendTokenRequest request);

}
