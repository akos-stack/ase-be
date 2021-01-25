package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;

public interface IUserProfileService {

    UserProfileDto findUserProfileByUserId(long id);

    UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request);

    UserProfileDto saveUserProfile(UserProfileDto userProfileDto, long principalId);

    EvaluatorDto saveEvaluator(EvaluatorDto evaluatorDto, long principalId);

    OwnerDto saveOwner(OwnerDto ownerDto, long principalId);

}
