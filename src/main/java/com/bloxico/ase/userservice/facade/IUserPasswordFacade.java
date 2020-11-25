package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.password.ForgottenPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.KnownPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;

public interface IUserPasswordFacade {

    void handleForgotPasswordRequest(ForgotPasswordRequest request);

    void resendPasswordToken(ResendTokenRequest request);

    void updateForgottenPassword(ForgottenPasswordUpdateRequest request);

    void updateKnownPassword(long principalId, KnownPasswordUpdateRequest request);

    void setNewPassword(long principalId, SetPasswordRequest request);

}
