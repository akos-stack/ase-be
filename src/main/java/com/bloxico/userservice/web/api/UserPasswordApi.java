package com.bloxico.userservice.web.api;

import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import com.bloxico.userservice.web.model.token.ResendTokenRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(value = "password", description = "User password API")
public interface UserPasswordApi {

    String FORGOT_PASSWORD_ENDPOINT = "/user/passwordForgot";
    String UPDATE_FORGOTTEN_PASSWORD_ENDPOINT = "/user/passwordForgotUpdate";
    String UPDATE_PASSWORD_ENDPOINT = "/user/passwordUpdate";
    String FORGOT_PASSWORD_TOKEN_RESEND_ENDPOINT = "/user/passwordTokenResend";
    String SET_PASSWORD_ENDPOINT = "/user/passwordSet";

    @PostMapping(value = FORGOT_PASSWORD_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Initialize forgot password procedure for user - this endpoint is being triggered when user clicks on a \"Forgot password\" link." +
            "Newly created password reset token will be sent to email provided in request body.")
    @ApiResponses({@ApiResponse(code = 200, message = "Initialization successful - user is being provided with an email that contains password reset token."),
            @ApiResponse(code = 409, message = "Email does not exist in the database."),
    })
    ResponseEntity<Void> initializeForgotPasswordProcedure(@Valid @RequestBody ForgotPasswordInitRequest forgotPasswordInitRequest, HttpServletRequest request);

    @PostMapping(value = UPDATE_FORGOTTEN_PASSWORD_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Update forgotten password with the provided password inside request body, it also contains token provided by email in order to validate user.")
    @ApiResponses({@ApiResponse(code = 200, message = "Forgotten password is updated sucessfully - user is able to log in using new password."),
            @ApiResponse(code = 404, message = "Token provided by request is either expired or does not exist - in both cases, uses has to reinitialize forgot password procedure."),
    })
    ResponseEntity<Void> updateForgottenPassword(@Valid @RequestBody ForgotPasswordChangeRequest forgotPasswordChangeRequest, HttpServletRequest request);

    @PostMapping(value = UPDATE_PASSWORD_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Update known password by providing the current and new password inside request body.")
    @ApiResponses({@ApiResponse(code = 200, message = "Update successful."),
            @ApiResponse(code = 409, message = "Current password does not match."),
    })
    ResponseEntity<Void> changePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request);

    @PostMapping(value = FORGOT_PASSWORD_TOKEN_RESEND_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "In case the mail has not been sent with password reset token token, resend email using existing token in the database.")
    @ApiResponses({@ApiResponse(code = 200, message = "Mail is sent successfully."),
            @ApiResponse(code = 409, message = "Could not find user associated to given email."),
            @ApiResponse(code = 404, message = "Token assigned to user is not found - probably expired."),
    })
    ResponseEntity<Void> resendPasswordResetToken(@RequestBody @Valid ResendTokenRequest resendTokenRequest, HttpServletRequest request);

    @PostMapping(value = SET_PASSWORD_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ApiOperation(value = "Set new password for user - endpoint used if user does not have a password (Integrated User)")
    @ApiResponses({@ApiResponse(code = 200, message = "Password is set successfully"),
            @ApiResponse(code = 400, message = "Bad password"),
    })
    ResponseEntity<Void> setNewPassword(@RequestBody @Valid SetPasswordRequest setPasswordRequest, HttpServletRequest request);

}
