package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.user.PagedUserProfileDataResponse;

public interface IUserManagementFacade {

    PagedUserProfileDataResponse searchUsers(String email, String role, int page, int size, String sort);

    void disableUser(long userId, long principalId);

    void blacklistTokens(long userId, long principalId);
}
