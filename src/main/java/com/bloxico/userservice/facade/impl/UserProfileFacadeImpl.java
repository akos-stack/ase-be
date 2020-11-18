package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.UpdateProfileDto;
import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.RegionDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;
import com.bloxico.userservice.facade.IUserProfileFacade;
import com.bloxico.userservice.services.user.IUserProfileService;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.util.mappers.UpdateProfileRequestMapper;
import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("userProfileFacadeImplOld")
@Slf4j
public class UserProfileFacadeImpl implements IUserProfileFacade {

    private IUserService userService;
    private IUserProfileService userProfileService;

    @Autowired
    public UserProfileFacadeImpl(IUserService userService,
                                 IUserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @Override
    public UserProfileDataResponse returnMyProfileData(String email) {
        log.info("Return my profile - start , email: {}", email);
        UserProfileDto userProfileDto = userProfileService.findProfileByEmail(email);
        CoinUserDto coinUserDto = userService.findUserByEmail(email);
        List<RegionDto> regionDtos = userService.getRegionList();


        UserProfileDataResponse dataResponse = new UserProfileDataResponse();
        dataResponse.setUserProfile(userProfileDto);
        dataResponse.setRegions(regionDtos);

        log.info("Return my profile - end , email: {}", email);
        return dataResponse;
    }

    @Override
    @Transactional
    public UserProfileDataResponse updateMyProfile(String email, UpdateProfileRequest updateProfileRequest) {
        log.info("Update my profile request - start , email {}, UpdateProfileRequest: {}", email, updateProfileRequest);
        UpdateProfileDto updateProfileDto = UpdateProfileRequestMapper.INSTANCE.requestToDto(updateProfileRequest);

        UserProfileDto userProfileDto = userProfileService.updateProfile(email, updateProfileDto);

        log.info("Update my profile request - end , email {}, UpdateProfileRequest: {}", email, updateProfileRequest);
        UserProfileDataResponse dataResponse = new UserProfileDataResponse();
        dataResponse.setUserProfile(userProfileDto);
        return dataResponse;
    }
}
