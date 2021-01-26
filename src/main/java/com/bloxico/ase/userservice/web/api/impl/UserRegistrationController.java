package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.web.api.UserRegistrationApi;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

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
    public ResponseEntity<Void> sendEvaluatorInvitation(EvaluatorInvitationRequest request, Principal principal) {
        var id = extractId(principal);
        userRegistrationFacade.sendEvaluatorInvitation(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> checkEvaluatorInvitation(String token) {
        userRegistrationFacade.checkEvaluatorInvitation(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendEvaluatorInvitation(EvaluatorInvitationResendRequest request) {
        userRegistrationFacade.resendEvaluatorInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> withdrawEvaluatorInvitation(EvaluatorInvitationWithdrawalRequest request) {
        userRegistrationFacade.withdrawEvaluatorInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EvaluatorDto> submitEvaluator(SubmitEvaluatorRequest request) {
        var response = userRegistrationFacade.submitEvaluator(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> requestEvaluatorRegistration(EvaluatorRegistrationRequest request, Principal principal) {
        var id = extractId(principal);
        userRegistrationFacade.requestEvaluatorRegistration(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedPendingEvaluatorDataResponse> searchPendingEvaluators(String email, int page, int size, String sort) {
        var arrayPendingEvaluatorDataResponse = userRegistrationFacade.searchPendingEvaluators(email, page, size, sort);
        return ResponseEntity.ok(arrayPendingEvaluatorDataResponse);
    }

    @Override
    public ResponseEntity<Resource> downloadEvaluatorResume(@Valid String email, Principal principal) {
        var id = extractId(principal);
        var response = userRegistrationFacade.downloadEvaluatorResume(email, id);
        return ResponseEntity.ok(response);
    }

}
