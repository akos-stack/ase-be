package com.bloxico.userservice.facade;

import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;

public interface IUserProfileFacade {

    UserProfileDataResponse returnMyProfileData(String email);

    void updateMyProfile(String email, UpdateProfileRequest updateProfileRequest);
}
