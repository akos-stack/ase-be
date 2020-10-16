package com.bloxico.userservice.web.api;

import com.bloxico.userservice.facade.IUserPasswordFacade;
import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import com.bloxico.userservice.web.model.token.ResendTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@RestController
public class UserPasswordController implements UserPasswordApi {

    private IUserPasswordFacade userPasswordFacade;

    @Autowired
    public UserPasswordController(IUserPasswordFacade userPasswordFacade) {
        this.userPasswordFacade = userPasswordFacade;
    }

    @Override
    public ResponseEntity<Void> initializeForgotPasswordProcedure(@RequestBody @Valid ForgotPasswordInitRequest forgotPasswordInitRequest, HttpServletRequest request) {

        userPasswordFacade.handleForgotPasswordRequest(forgotPasswordInitRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateForgottenPassword(@RequestBody @Valid ForgotPasswordChangeRequest forgotPasswordChangeRequest, HttpServletRequest request) {
        userPasswordFacade.updateForgottenPassword(forgotPasswordChangeRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> changePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String email = principal.getName();

        userPasswordFacade.updateKnownPassword(email, updatePasswordRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendPasswordResetToken(@RequestBody @Valid ResendTokenRequest resendTokenRequest, HttpServletRequest request) {
        userPasswordFacade.resendPasswordToken(resendTokenRequest.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> setNewPassword(@RequestBody @Valid SetPasswordRequest setPasswordRequest, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        String email = principal.getName();

        userPasswordFacade.setNewPasword(email, setPasswordRequest.getPassword());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
