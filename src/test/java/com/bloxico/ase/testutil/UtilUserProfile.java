package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.profile.UserProfile;
import com.bloxico.ase.userservice.repository.user.profile.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.user.Role.ART_OWNER;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

@Component
public class UtilUserProfile {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private UserProfileServiceImpl userProfileService;

    public UserProfile savedUserProfile(long userId) {
        var userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setFirstName(genUUID());
        userProfile.setLastName(genUUID());
        userProfile.setBirthday(LocalDate.now().minusYears(20));
        userProfile.setGender(genUUID());
        userProfile.setPhone(genUUID());
        userProfile.setLocation(utilLocation.savedLocation());
        userProfile.setCreatorId(userId);
        return userProfileRepository.saveAndFlush(userProfile);
    }

    public UserProfile savedUserProfile() {
        return savedUserProfile(utilUser.savedUser().getId());
    }

    public UserProfileDto savedUserProfileDto(long userId) {
        return MAPPER.toDto(savedUserProfile(userId));
    }

    public UserProfileDto savedUserProfileDto() {
        return savedUserProfileDto(utilUser.savedUser().getId());
    }

    public SubmitEvaluatorRequest newSubmitUninvitedEvaluatorRequest() {
        return newSubmitUninvitedEvaluatorRequest(utilLocation.savedCountry().getName());
    }

    public SubmitEvaluatorRequest newSubmitUninvitedEvaluatorRequest(String country) {
        var email = genEmail();
        var password = genPassword();
        return new SubmitEvaluatorRequest(
                genUUID(), genUUID(), password,
                email, genUUID(), genUUID(),
                genUUID(), LocalDate.now(),
                genUUID(), country, genUUID(), ONE, TEN);
    }

    public SubmitArtOwnerRequest newSubmitArtOwnerRequest() {
        return newSubmitArtOwnerRequest(utilLocation.savedCountry().getName());
    }

    public SubmitArtOwnerRequest newSubmitArtOwnerRequest(String country) {
        return new SubmitArtOwnerRequest(
                genUUID(), genPassword(),
                genEmail(), genUUID(), genUUID(),
                genUUID(), LocalDate.now(),
                genUUID(), country, genUUID(), ONE, TEN);
    }

    public ArtOwnerDto savedArtOwnerDto() {
        return savedArtOwnerDto(utilUser.savedUser().getId());
    }

    public ArtOwnerDto savedArtOwnerDto(long userId) {
        var userProfileDto = savedUserProfileDto(userId);
        var principalId = userProfileDto.getUserId();
        var artOwnerDto = new ArtOwnerDto();
        artOwnerDto.setUserProfile(userProfileDto);
        var response = userProfileService.saveArtOwner(artOwnerDto, principalId);
        utilUser.addRoleToUserWithId(ART_OWNER, userId);
        return response;
    }

}
