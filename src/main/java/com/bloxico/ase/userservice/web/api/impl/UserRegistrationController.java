package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.web.api.UserRegistrationApi;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegistrationController implements UserRegistrationApi {

    @Autowired
    private IUserRegistrationFacade userRegistrationFacade;

    @Override
    public ResponseEntity<RegistrationResponse> registration(RegistrationRequest request) {
        var response = userRegistrationFacade.registerUserWithVerificationToken(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> confirmRegistration(TokenValidationRequest request) {
        userRegistrationFacade.handleTokenValidation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> refreshRegistrationToken(String token) {
        userRegistrationFacade.refreshExpiredToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendRegistrationToken(ResendTokenRequest request) {
        userRegistrationFacade.resendVerificationToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
