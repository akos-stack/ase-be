package com.bloxico.userservice.web.api;

import com.bloxico.userservice.web.model.registration.RegistrationDataResponse;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.userservice.web.model.token.TokenValidityRequest;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(value = "registration", description = "Registration API")
public interface UserRegistrationApi {

    String REGISTRATION_ENDPOINT = "/user/registration";
    String REGISTRATION_CONFIRMATION_ENDPOINT = "/user/registrationConfirm";
    String REGISTRATION_TOKEN_REFRESH_ENDPOINT = "/user/registrationTokenRefresh";
    String REGISTRATION_TOKEN_RESEND_ENDPOINT = "/user/registrationTokenResend";
    String REGISTRATION_PAGE_DATA_ENDPOINT = "/user/registrationData";

    String TOKEN_PARAM = "token";

    @PostMapping(value = REGISTRATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Register user - create disabled user in the database and send verification token using provided email that will be used to enable user.")
    @ApiResponses({@ApiResponse(code = 200, message = "Registration successfully done"),
            @ApiResponse(code = 409, message = "User already exists."),
            @ApiResponse(code = 500, message = "Entity not found - either region or role, details are provided in response object.")
    })
    ResponseEntity<RegistrationResponse> registerCoinUser(@Valid @RequestBody RegistrationRequest registrationRequest, HttpServletRequest request);

    @PostMapping(value = REGISTRATION_CONFIRMATION_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Token confirmation endpoint - accepts verification token provided by email, and if correct, enables user.")
    @ApiResponses({@ApiResponse(code = 200, message = "Validation successful - user is enabled and able to log in."),
            @ApiResponse(code = 404, message = "Token provided is either not found or expired - see details provided in response object"),
    })
    ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody TokenValidityRequest tokenValidityRequest, HttpServletRequest request);

    @ApiOperation(value = "Endpoint that has to be called if token that is being validated has expired - this will rarely happen since all expired tokens are being deleted within 24 hours " +
            "but in case if user tries to validate token that is expired, this endpoint needs to be called in order to refresh the token.")
    @ApiResponses({@ApiResponse(code = 200, message = "Token is being refreshed successfully and email has been sent with updated token value."),
            @ApiResponse(code = 404, message = "Token provided is not found."),
    })
    @GetMapping(value = REGISTRATION_TOKEN_REFRESH_ENDPOINT)
    ResponseEntity<Void> refreshVerificationToken(@ApiParam(value = "Expired token value", required = true) @RequestParam(TOKEN_PARAM) String token, HttpServletRequest request);

    @PostMapping(value = REGISTRATION_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "In case the mail has not been sent with verification token, resend email using existing token in the database.")
    @ApiResponses({@ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 409, message = "Could not find user associated with given email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found - probably expired."),
    })
    ResponseEntity<Void> resendRegistrationMail(@Valid @RequestBody ResendTokenRequest resendTokenRequest, HttpServletRequest request);


    @ApiOperation(value = "Get data necessary for registration page.")
    @GetMapping(value = REGISTRATION_PAGE_DATA_ENDPOINT)
    ResponseEntity<RegistrationDataResponse> getRegistrationData(HttpServletRequest request);
}
