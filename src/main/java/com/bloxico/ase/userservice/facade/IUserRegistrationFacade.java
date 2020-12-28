package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;

public interface IUserRegistrationFacade {

    RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request);

    void handleTokenValidation(TokenValidationRequest request);

    void refreshExpiredToken(String expiredTokenValue);

    void resendVerificationToken(ResendTokenRequest request);

    void sendEvaluatorInvitation(EvaluatorInvitationRequest request, long principalId);

    void resendEvaluatorInvitation(EvaluatorInvitationResendRequest request);

    void withdrawEvaluatorInvitation(EvaluatorInvitationWithdrawalRequest request);

    void requestEvaluatorRegistration(EvaluatorRegistrationRequest request, long principalId);

}
