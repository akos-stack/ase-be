package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;

import java.util.List;

public interface IUserProfileService {

    UserProfileDto findUserProfileById(long id);

    UserProfileDto findUserProfileByEmail(String email);

    UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request);

    UserProfileDto saveEnabledUserProfile(UserProfileDto userProfileDto);

    UserProfileDto updateLocation(UserProfileDto userProfileDto, LocationDto locationDto);

    EvaluatorDto saveEvaluator(EvaluatorDto evaluatorDto, long principalId);

    void disableUser(long userId, long principalId);

    List<UserProfileDto> findUsersByEmailOrRole(String email, Role.UserRole role, int page, int size, String sort);

}
