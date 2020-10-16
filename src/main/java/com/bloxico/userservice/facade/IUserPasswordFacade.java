package com.bloxico.userservice.facade;

import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;

public interface IUserPasswordFacade {

    void handleForgotPasswordRequest(ForgotPasswordInitRequest forgotPasswordInitRequest);

    void updateForgottenPassword(ForgotPasswordChangeRequest forgotPasswordChangeRequest);

    void updateKnownPassword(String email, UpdatePasswordRequest updatePasswordRequest);

    void resendPasswordToken(String email);

    void setNewPasword(String email, String password);
}
