package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.user.SearchUsersResponse;
import com.bloxico.ase.userservice.web.model.user.SearchUsersRequest;

public interface IUserManagementFacade {

    SearchUsersResponse searchUsers(SearchUsersRequest request, PageRequest page);

    void disableUser(long userId);

    void blacklistTokens(long userId);

}
