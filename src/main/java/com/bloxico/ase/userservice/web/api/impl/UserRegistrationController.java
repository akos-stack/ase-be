package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.web.api.UserRegistrationApi;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.PrincipalUtil.extractId;

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

    @Override
    public ResponseEntity<Void> sendEvaluatorInvitation(@Valid EvaluatorInvitationRequest request, Principal principal) {
        var id = extractId(principal);
        userRegistrationFacade.sendEvaluatorInvitation(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendEvaluatorInvitation(@Valid EvaluatorInvitationResendRequest request) {
        userRegistrationFacade.resendEvaluatorInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> withdrawEvaluatorInvitation(@Valid EvaluatorInvitationWithdrawalRequest request) {
        userRegistrationFacade.withdrawEvaluatorInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> requestEvaluatorRegistration(@Valid EvaluatorRegistrationRequest request, Principal principal) {
        var id = extractId(principal);
        userRegistrationFacade.requestEvaluatorRegistration(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
