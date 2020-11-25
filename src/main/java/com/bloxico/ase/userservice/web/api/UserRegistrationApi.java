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

@Api(value = "registration")
public interface UserRegistrationApi {

    String REGISTRATION_ENDPOINT               = "/user/registration";
    String REGISTRATION_CONFIRM_ENDPOINT       = "/user/registration/confirm";
    String REGISTRATION_TOKEN_REFRESH_ENDPOINT = "/user/registration/token/refresh";
    String REGISTRATION_TOKEN_RESEND_ENDPOINT  = "/user/registration/token/resend";

    String TOKEN_PARAM = "token";

    @PostMapping(
            value = REGISTRATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Creates disabled user in the database and sends verification token to the provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Registration successfully done."),
            @ApiResponse(code = 400, message = "Values `password` and `match_password` do not match."),
            @ApiResponse(code = 409, message = "User already exists.")
    })
    ResponseEntity<RegistrationResponse> registration(@Valid @RequestBody RegistrationRequest request);

    @PostMapping(
            value = REGISTRATION_CONFIRM_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Accepts verification token provided by email and, if correct, enables user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Validation successful - user is now able to log in."),
            @ApiResponse(code = 404, message = "Could not find user associated with the given email."),
            @ApiResponse(code = 404, message = "Provided token is either not found or expired.")
    })
    ResponseEntity<Void> confirmRegistration(@Valid @RequestBody TokenValidationRequest request);

    @ApiOperation(
            value = "Refreshes expired registration token.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token is refreshed and email has been sent with updated token value."),
            @ApiResponse(code = 404, message = "Provided token is not found.")
    })
    @GetMapping(value = REGISTRATION_TOKEN_REFRESH_ENDPOINT)
    ResponseEntity<Void> refreshRegistrationToken(@ApiParam(value = "Expired token value", required = true)
                                                  @RequestParam(TOKEN_PARAM) String token);

    @PostMapping(
            value = REGISTRATION_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Resends email with existing token in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 404, message = "Could not find user associated with given email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found, it probably expired.")
    })
    ResponseEntity<Void> resendRegistrationToken(@Valid @RequestBody ResendTokenRequest request);

}
