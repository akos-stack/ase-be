package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserPasswordFacade;
import com.bloxico.ase.userservice.web.api.UserPasswordApi;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.password.ForgottenPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.KnownPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class UserPasswordController implements UserPasswordApi {

    @Autowired
    private IUserPasswordFacade userPasswordFacade;

    @Override
    public ResponseEntity<Void> initForgotPasswordProcedure(ForgotPasswordRequest request) {
        userPasswordFacade.handleForgotPasswordRequest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendPasswordResetToken(ResendTokenRequest request) {
        userPasswordFacade.resendPasswordToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateForgottenPassword(ForgottenPasswordUpdateRequest request) {
        userPasswordFacade.updateForgottenPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateKnownPassword(KnownPasswordUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        userPasswordFacade.updateKnownPassword(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> setNewPassword(SetPasswordRequest request, Principal principal) {
        var id = extractId(principal);
        userPasswordFacade.setNewPassword(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
