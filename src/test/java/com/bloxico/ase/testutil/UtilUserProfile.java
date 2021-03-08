package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.profile.UserProfile;
import com.bloxico.ase.userservice.facade.impl.UserRegistrationFacadeImpl;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.user.profile.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.user.Role.ART_OWNER;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

@Component
public class UtilUserProfile {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private UserProfileServiceImpl userProfileService;
    @Autowired private PendingEvaluatorRepository pendingEvaluatorRepository;
    @Autowired private UserRegistrationFacadeImpl userRegistrationFacade;

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
        var email = genEmail();
        var password = genPassword();
        var country = utilLocation.savedCountry().getName();
        return new SubmitEvaluatorRequest(
                genUUID(), genUUID(), password,
                email, genUUID(), genUUID(),
                genUUID(), LocalDate.now(),
                genUUID(), country, genUUID(),
                ONE, TEN, genMultipartFile(IMAGE));
    }

    public SubmitArtOwnerRequest newSubmitArtOwnerRequest() {
        var country = utilLocation.savedCountry().getName();
        return new SubmitArtOwnerRequest(
                genUUID(), genPassword(),
                genEmail(), genUUID(), genUUID(),
                genUUID(), LocalDate.now(),
                genUUID(), country, genUUID(),
                ONE, TEN, genMultipartFile(IMAGE));
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

    public Map<String, String> genUserProfileFormParams() {
        var map = new HashMap<String, String>();
        map.put("username", genUUID());
        map.put("password", genPassword());
        map.put("email", genEmail());
        map.put("firstName", genUUID());
        map.put("lastName", genUUID());
        map.put("phone", genUUID());
        map.put("birthday", genPastLD().toString());
        map.put("gender", genUUID());
        map.put("country", utilLocation.savedCountry().getName());
        map.put("address", genUUID());
        map.put("latitude", genPosBigDecimal(100).toString());
        map.put("longitude", genPosBigDecimal(100).toString());
        return map;
    }

    public Map<String, String> genSaveEvaluatorFormParams(String token, String email) {
        var map = genUserProfileFormParams();
        map.put("token", token);
        map.put("email", email);
        return map;
    }

    public Map<String, String> genSaveEvaluatorFormParams() {
        var email = genEmail();
        var principalId = utilUser.savedAdmin().getId();
        userRegistrationFacade.sendEvaluatorInvitation(
                new EvaluatorInvitationRequest(email), principalId);
        var token = pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow()
                .getToken();
        return genSaveEvaluatorFormParams(token, email);
    }

    public Map<String, String> genSaveArtOwnerFormParams() {
        return genUserProfileFormParams();
    }

}
