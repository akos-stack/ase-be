package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;

public interface IUserProfileFacade {

    UserProfileDataResponse returnMyProfileData();

    UserProfileDataResponse updateMyProfile(UpdateUserProfileRequest request);

}
