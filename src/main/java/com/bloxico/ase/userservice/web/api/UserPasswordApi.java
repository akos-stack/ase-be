package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(value = "password")
public interface UserPasswordApi {

    // @formatter:off
    String PASSWORD_FORGOT_ENDPOINT           = "/user/password/forgot";
    String PASSWORD_TOKEN_RESEND_ENDPOINT     = "/user/password/token/resend";
    String PASSWORD_UPDATE_FORGOTTEN_ENDPOINT = "/user/password/update/forgotten";
    String PASSWORD_UPDATE_ENDPOINT           = "/user/password/update";
    String PASSWORD_SET_ENDPOINT              = "/management/user/password/set";
    // @formatter:on

    @PostMapping(
            value = PASSWORD_FORGOT_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Triggered when user clicks on the 'Forgot password' link. " +
            "Newly created password-reset token will be sent to the provided email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password-reset token is sent successfully."),
            @ApiResponse(code = 404, message = "Could not find the user associated with provided email.")
    })
    ResponseEntity<Void> initForgotPasswordProcedure(@Valid @RequestBody ForgotPasswordRequest request);

    @PostMapping(
            value = PASSWORD_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Resends email with existing token in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 404, message = "Could not find the user associated with provided email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found, it probably expired.")
    })
    ResponseEntity<Void> resendPasswordResetToken(@Valid @RequestBody ResendTokenRequest request);

    @PostMapping(
            value = PASSWORD_UPDATE_FORGOTTEN_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Replaces forgotten password with provided password. " +
            "Password-reset token is also required in order to validate user.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Forgotten password is updated successfully."),
            @ApiResponse(code = 404, message = "Could not find the user associated with provided email."),
            @ApiResponse(code = 404, message = "Provided token is either expired or does not exist.")
    })
    ResponseEntity<Void> updateForgottenPassword(@Valid @RequestBody ForgottenPasswordUpdateRequest request);

    @PostMapping(
            value = PASSWORD_UPDATE_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Replaces current password with a new password.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password is updated successfully."),
            @ApiResponse(code = 400, message = "Current password does not match the `old_password`.")
    })
    ResponseEntity<Void> updateKnownPassword(@Valid @RequestBody KnownPasswordUpdateRequest request);

    @PostMapping(
            value = PASSWORD_SET_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'set_password')")
    @ApiOperation(value = "Sets a new password for user who does not have a password (i.e. integrated user).")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password is set successfully.")
    })
    ResponseEntity<Void> setNewPassword(@Valid @RequestBody SetPasswordRequest request);

}
