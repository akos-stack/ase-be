package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;

public interface IUserPasswordFacade {

    void handleForgotPasswordRequest(ForgotPasswordRequest request);

    void resendPasswordToken(ResendTokenRequest request);

    void updateForgottenPassword(ForgottenPasswordUpdateRequest request);

    void updateKnownPassword(KnownPasswordUpdateRequest request);

    void setNewPassword(SetPasswordRequest request);

}
