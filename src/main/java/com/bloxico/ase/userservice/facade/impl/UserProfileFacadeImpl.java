package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserProfileFacade;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.config.security.AseSecurityContext.getPrincipalId;

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
    public UserProfileDataResponse returnMyProfileData() {
        log.info("UserProfileFacadeImpl.returnMyProfileData - start");
        var userProfileDto = userProfileService.findUserProfileByUserId(getPrincipalId());
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.returnMyProfileData - end");
        return response;
    }

    @Override
    public UserProfileDataResponse updateMyProfile(UpdateUserProfileRequest request) {
        log.info("UserProfileFacadeImpl.updateMyProfile - start | request: {}", request);
        var userProfileDto = userProfileService.updateUserProfile(getPrincipalId(), request);
        var response = new UserProfileDataResponse(userProfileDto);
        log.info("UserProfileFacadeImpl.updateMyProfile - end | request: {}", request);
        return response;
    }

}
