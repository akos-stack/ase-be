package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.core.io.ByteArrayResource;

public interface IUserRegistrationFacade {

    RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request);

    void handleTokenValidation(TokenValidationRequest request);

    void refreshExpiredToken(String expiredTokenValue);

    void resendVerificationToken(ResendTokenRequest request);

    void sendEvaluatorInvitation(EvaluatorInvitationRequest request, long principalId);

    void checkEvaluatorInvitation(String token);

    void resendEvaluatorInvitation(EvaluatorInvitationResendRequest request);

    void withdrawEvaluatorInvitation(EvaluatorInvitationWithdrawalRequest request);

    EvaluatorDto submitEvaluator(SubmitEvaluatorRequest request);

    void requestEvaluatorRegistration(EvaluatorRegistrationRequest request, long principalId);

    PagedPendingEvaluatorDataResponse searchPendingEvaluators(String email, int page, int size, String sort);

    ByteArrayResource downloadEvaluatorResume(String email, long principalId);
}
