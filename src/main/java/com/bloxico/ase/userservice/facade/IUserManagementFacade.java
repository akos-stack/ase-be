package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;

public interface IUserManagementFacade {

    ArrayUserProfileDataResponse searchUsers(String email, Role.UserRole role, int page, int size, String sort);

    void disableUser(long userId, long principalId);

    void blacklistTokens(long userId, long principalId);
}
