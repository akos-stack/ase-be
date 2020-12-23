package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;

public interface IUserSearchFacade {

    ArrayUserProfileDataResponse searchUsers(String email);
}
