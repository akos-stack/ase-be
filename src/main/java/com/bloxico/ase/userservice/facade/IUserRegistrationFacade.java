package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.DownloadEvaluatorResumeRequest;
import com.bloxico.ase.userservice.web.model.user.RefreshRegistrationTokenRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.core.io.ByteArrayResource;

public interface IUserRegistrationFacade {

    RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request);

    void handleTokenValidation(TokenValidationRequest request);

    void refreshExpiredToken(RefreshRegistrationTokenRequest request);

    void resendVerificationToken(ResendTokenRequest request);

    void sendEvaluatorInvitation(EvaluatorInvitationRequest request);

    void checkEvaluatorInvitation(String token);

    void resendEvaluatorInvitation(EvaluatorInvitationResendRequest request);

    void withdrawEvaluatorInvitation(EvaluatorInvitationWithdrawalRequest request);

    EvaluatorDto submitEvaluator(SubmitEvaluatorRequest request);

    ArtOwnerDto submitArtOwner(SubmitArtOwnerRequest request);

    void requestEvaluatorRegistration(EvaluatorRegistrationRequest request);

    SearchPendingEvaluatorsResponse searchPendingEvaluators(SearchPendingEvaluatorsRequest request, PageRequest page);

    ByteArrayResource downloadEvaluatorResume(DownloadEvaluatorResumeRequest request);

    void sendHostInvitation(HostInvitationRequest request);

    void withdrawHostInvitation(HostInvitationWithdrawalRequest request);
}
