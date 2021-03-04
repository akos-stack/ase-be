package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.user.PagedUserDataResponse;

public interface IUserManagementFacade {

    PagedUserDataResponse searchUsers(String email, String role, int page, int size, String sort);

    void disableUser(long userId);

    void blacklistTokens(long userId);

}
