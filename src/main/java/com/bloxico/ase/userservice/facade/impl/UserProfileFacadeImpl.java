package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserProfileFacade;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserProfileFacadeImpl implements IUserProfileFacade {

    private final IUserProfileService userProfileService;

    @Autowired
    public UserProfileFacadeImpl(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public UserProfileDataResponse returnMyProfileData(long principalId) {
        log.info("UserProfileFacadeImpl.returnMyProfileData - start | principalId: {}", principalId);
        var userProfileDto = userProfileService.findUserProfileById(principalId);
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.returnMyProfileData - end | principalId: {}", principalId);
        return response;
    }

    @Override
    public UserProfileDataResponse updateMyProfile(long principalId, UpdateUserProfileRequest request) {
        log.info("UserProfileFacadeImpl.updateMyProfile - start | principalId: {}, request: {}", principalId, request);
        var userProfileDto = userProfileService.updateUserProfile(principalId, request);
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.updateMyProfile - end | principalId: {}, request: {}", principalId, request);
        return response;
    }

}
