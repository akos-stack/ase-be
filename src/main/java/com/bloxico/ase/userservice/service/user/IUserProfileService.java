package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;

public interface IUserProfileService {

    UserProfileDto findUserProfileById(long id);

    UserProfileDto findUserProfileByEmail(String email);

    UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request);

}
