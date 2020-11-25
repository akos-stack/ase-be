package com.bloxico.ase.userservice.service.user;

public interface IUserPasswordService {

    void updateForgottenPassword(long userProfileId, String newPassword);

    void updateKnownPassword(long principalId, String oldPassword, String newPassword);

    void setNewPassword(long principalId, String password);

}
