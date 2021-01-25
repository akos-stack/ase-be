package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.OwnerDto;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.*;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitOwnerRequest;

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

    OwnerDto submitOwner(SubmitOwnerRequest request);

    void requestEvaluatorRegistration(EvaluatorRegistrationRequest request, long principalId);

    ArrayPendingEvaluatorDataResponse searchPendingEvaluators(String email, int page, int size, String sort);

}
