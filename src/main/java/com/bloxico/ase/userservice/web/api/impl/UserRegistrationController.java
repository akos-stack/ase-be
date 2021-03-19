package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.web.api.UserRegistrationApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.DownloadEvaluatorResumeRequest;
import com.bloxico.ase.userservice.web.model.user.RefreshRegistrationTokenRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
    public ResponseEntity<Void> refreshRegistrationToken(RefreshRegistrationTokenRequest request) {
        userRegistrationFacade.refreshExpiredToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> resendRegistrationToken(ResendTokenRequest request) {
        userRegistrationFacade.resendVerificationToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> sendEvaluatorInvitation(EvaluatorInvitationRequest request) {
        userRegistrationFacade.sendEvaluatorInvitation(request);
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
    public ResponseEntity<ArtOwnerDto> submitArtOwner(SubmitArtOwnerRequest request) {
        var response = userRegistrationFacade.submitArtOwner(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> requestEvaluatorRegistration(EvaluatorRegistrationRequest request) {
        userRegistrationFacade.requestEvaluatorRegistration(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SearchPendingEvaluatorsResponse> searchPendingEvaluators(
            SearchPendingEvaluatorsRequest request, PageRequest page)
    {
        var arrayPendingEvaluatorDataResponse = userRegistrationFacade.searchPendingEvaluators(request, page);
        return ResponseEntity.ok(arrayPendingEvaluatorDataResponse);
    }

    @Override
    public ResponseEntity<Resource> downloadEvaluatorResume(DownloadEvaluatorResumeRequest request) {
        var response = userRegistrationFacade.downloadEvaluatorResume(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> sendHostInvitation(HostInvitationRequest request) {
        userRegistrationFacade.sendHostInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> withdrawHostInvitation(HostInvitationWithdrawalRequest request) {
        userRegistrationFacade.withdrawHostInvitation(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
