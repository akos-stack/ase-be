package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test
    public void findUserProfileByUserId_notFound() {
        assertThrows(
                UserException.class,
                () -> userProfileService.findUserProfileByUserId(-1));
    }

    @Test
    public void findUserProfileByUserId_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileByUserId(userProfileDto.getUserId()));
    }

    @Test
    public void updateUserProfile_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userProfileService.updateUserProfile(1, null));
    }

    @Test
    public void updateUserProfile_userNotFound() {
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        assertThrows(
                UserException.class,
                () -> userProfileService.updateUserProfile(-1, request));
    }

    @Test
    public void updateUserProfile() {
        var id = mockUtil.savedUserProfile().getUserId();
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        var updated = userProfileService.updateUserProfile(id, request);
        assertEquals(request.getFirstName(), updated.getFirstName());
        assertEquals(request.getLastName(), updated.getLastName());
        assertEquals(request.getPhone(), updated.getPhone());
    }

    @Test
    public void saveEvaluator_nullEvaluatorDto() {
        var principalId = mockUtil.savedUserProfileDto().getUserId();
        assertThrows(
                NullPointerException.class,
                () -> userProfileService.saveEvaluator(null, principalId));
    }

    @Test
    public void saveEvaluator() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var principalId = userProfileDto.getUserId();
        var evaluatorDto = new EvaluatorDto();
        evaluatorDto.setUserProfile(userProfileDto);
        evaluatorDto = userProfileService.saveEvaluator(evaluatorDto, principalId);
        assertNotNull(evaluatorDto.getId());
    }

    @Test
    public void saveArtOwner_nullOwnerDto() {
        var principalId = mockUtil.savedUserProfileDto().getUserId();
        assertThrows(
                NullPointerException.class,
                () -> userProfileService.saveArtOwner(null, principalId));
    }

    @Test
    public void saveArtOwner() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var principalId = userProfileDto.getUserId();
        var artOwnerDto = new ArtOwnerDto();
        artOwnerDto.setUserProfile(userProfileDto);
        artOwnerDto = userProfileService.saveArtOwner(artOwnerDto, principalId);
        assertNotNull(artOwnerDto.getId());
    }

}
