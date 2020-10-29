package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.UpdateProfileDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;
import com.bloxico.userservice.entities.user.Region;
import com.bloxico.userservice.entities.user.UserProfile;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.RegionRepository;
import com.bloxico.userservice.repository.user.UserProfileRepository;
import com.bloxico.userservice.services.user.IUserProfileService;
import com.bloxico.userservice.util.mappers.EntityDataMapper;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service("userProfileServiceImplOld")
public class UserProfileServiceImpl implements IUserProfileService {

    private UserProfileRepository userProfileRepository;
    private RegionRepository regionRepository;
    private EntityDataMapper entityDataMapper = EntityDataMapper.INSTANCE;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, RegionRepository regionRepository) {
        this.userProfileRepository = userProfileRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public UserProfileDto findProfileByEmail(String email) {
        log.debug("Find user profile by email - start , email: {}", email);

        Optional<UserProfile> op = userProfileRepository.findByEmail(email);

        UserProfileDto userProfileDto = entityDataMapper.userProfileToDto(
                op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_PROFILE_DOES_NOT_EXIST.getCode())));

        log.debug("Find user profile by email - end , email: {} , profile: {}", email, userProfileDto);

        return userProfileDto;
    }

    @Override
    public void updateProfile(String email, UpdateProfileDto updateProfileDto) {
        log.debug("Update profile - start , email: {} - UpdateProfile data", updateProfileDto);

        Optional<UserProfile> userProfileOptional = userProfileRepository.findByEmail(email);
        UserProfile userProfile = userProfileOptional.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_PROFILE_DOES_NOT_EXIST.getCode()));

        userProfile.setName(updateProfileDto.getName());
        userProfile.setCity(updateProfileDto.getCity());

        Optional<Region> regionOptional = regionRepository.findByRegionName(updateProfileDto.getRegion());
        userProfile.setRegion(regionOptional.orElseThrow(() -> new EntityNotFoundException(ErrorCodes.REGION_NOT_FOUND.getCode())));

        userProfileRepository.save(userProfile);

        log.debug("Update profile - end , email: {}", email);
    }
}
