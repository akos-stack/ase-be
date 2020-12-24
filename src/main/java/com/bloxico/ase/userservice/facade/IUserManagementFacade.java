package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;

public interface IUserManagementFacade {

    ArrayUserProfileDataResponse searchUsers(String email, int page, int size);

    void disableUser(long userId, long principalId);

    void blacklistTokens(long userId, long principalId);
}
