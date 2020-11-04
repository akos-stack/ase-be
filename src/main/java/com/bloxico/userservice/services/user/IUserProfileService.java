package com.bloxico.userservice.services.user;

import com.bloxico.userservice.dto.UpdateProfileDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;

public interface IUserProfileService {
    UserProfileDto findProfileByEmail(String email);

    UserProfileDto updateProfile(String email, UpdateProfileDto updateProfileDto);
}
