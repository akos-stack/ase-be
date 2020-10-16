package com.bloxico.userservice.services.user;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.dto.UpdatePasswordDto;

public interface IUserPasswordService {

    void updateForgottenPassword(long userId, ForgotPasswordDto forgotPasswordDto);

    void updateKnownPassword(String email, UpdatePasswordDto updatePasswordDto);

    void setNewPassword(String email, String newPassword);
}
