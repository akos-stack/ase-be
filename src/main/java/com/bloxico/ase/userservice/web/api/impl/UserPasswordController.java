package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserPasswordFacade;
import com.bloxico.ase.userservice.web.api.UserPasswordApi;
import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> updateKnownPassword(KnownPasswordUpdateRequest request) {
        userPasswordFacade.updateKnownPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> setNewPassword(SetPasswordRequest request) {
        userPasswordFacade.setNewPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
