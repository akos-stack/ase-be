package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test(expected = UserProfileException.class)
    public void findUserProfileById_notFound() {
        userProfileService.findUserProfileById(-1);
    }

    @Test
    public void findUserProfileById_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileById(userProfileDto.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void findUserProfileByEmail_nullEmail() {
        userProfileService.findUserProfileByEmail(null);
    }

    @Test(expected = UserProfileException.class)
    public void findUserProfileByEmail_notFound() {
        userProfileService.findUserProfileByEmail(uuid());
    }

    @Test
    public void findUserProfileByEmail_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileByEmail(userProfileDto.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void updateUserProfile_nullRequest() {
        userProfileService.updateUserProfile(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateUserProfile_notFound() {
        var request = new UpdateUserProfileRequest("John", null);
        userProfileService.updateUserProfile(-1, request);
    }

    @Test
    public void updateUserProfile() {
        var id = mockUtil.savedUserProfile().getId();
        var request = new UpdateUserProfileRequest("John", "007008123");
        var updated = userProfileService.updateUserProfile(id, request);
        assertEquals(request.getName(), updated.getName());
        assertEquals(request.getPhone(), updated.getPhone());
    }

    @Test(expected = UserProfileException.class)
    public void disableUser_notFound() {
        var adminId = mockUtil.savedAdmin().getId();
        userProfileService.disableUser(-1, adminId);
    }

    @Test
    public void disableUser() {
        var adminId = mockUtil.savedAdmin().getId();
        var userId = mockUtil.savedUserProfile().getId();
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userProfileService.disableUser(userId, adminId);
        assertFalse(userProfileService.findUserProfileById(userId).getEnabled());
    }

}
