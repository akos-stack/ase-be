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
    public UserProfileDataResponse returnMyProfileData(long id) {
        log.info("UserProfileFacadeImpl.returnMyProfileData - start | id: {}", id);
        var userProfileDto = userProfileService.findUserById(id);
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.returnMyProfileData - end | id: {}", id);
        return response;
    }

    @Override
    public UserProfileDataResponse updateMyProfile(long id, UpdateUserProfileRequest request) {
        log.info("UserProfileFacadeImpl.updateMyProfile - start | id: {}, request: {}", id, request);
        var userProfileDto = userProfileService.updateUserProfile(id, request);
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.updateMyProfile - end | id: {}, request: {}", id, request);
        return response;
    }

}
