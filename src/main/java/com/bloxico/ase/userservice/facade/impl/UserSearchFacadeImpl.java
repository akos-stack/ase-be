package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserSearchFacade;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserSearchFacadeImpl implements IUserSearchFacade {

    private final IUserProfileService userProfileService;

    @Autowired
    public UserSearchFacadeImpl(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public ArrayUserProfileDataResponse searchUsers(String email) {
        log.info("UserSearchFacadeImpl.searchUsers - start | email: {}", email);
        var userProfileDtos = userProfileService.findUsersByEmail(email, 0, 100);
        var response = new ArrayUserProfileDataResponse(userProfileDtos);
        log.info("UserSearchFacadeImpl.searchUsers - end | email: {}", email);
        return response;
    }
}
