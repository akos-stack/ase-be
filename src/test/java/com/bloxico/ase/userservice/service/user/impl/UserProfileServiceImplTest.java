package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.OwnerDto;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test(expected = UserException.class)
    public void findUserProfileByUserId_notFound() {
        userProfileService.findUserProfileByUserId(-1);
    }

    @Test
    public void findUserProfileByUserId_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileByUserId(userProfileDto.getUserId()));
    }

    @Test(expected = NullPointerException.class)
    public void updateUserProfile_nullRequest() {
        userProfileService.updateUserProfile(1, null);
    }

    @Test(expected = UserException.class)
    public void updateUserProfile_userNotFound() {
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        userProfileService.updateUserProfile(-1, request);
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

    @Test(expected = NullPointerException.class)
    public void saveEvaluator_nullEvaluatorDto() {
        var principalId = mockUtil.savedUserProfileDto().getUserId();
        userProfileService.saveEvaluator(null, principalId);
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

    @Test(expected = NullPointerException.class)
    public void saveOwner_nullOwnerDto() {
        var principalId = mockUtil.savedUserProfileDto().getUserId();
        userProfileService.saveOwner(null, principalId);
    }

    @Test
    public void saveOwner() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var principalId = userProfileDto.getUserId();
        var ownerDto = new OwnerDto();
        ownerDto.setUserProfile(userProfileDto);
        ownerDto = userProfileService.saveOwner(ownerDto, principalId);
        assertNotNull(ownerDto.getId());
    }

}
