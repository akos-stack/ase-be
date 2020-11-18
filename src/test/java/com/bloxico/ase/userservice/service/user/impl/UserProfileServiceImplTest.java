package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.config.AseUserDetails;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test(expected = UserProfileException.class)
    public void findUserById_notFound() {
        userProfileService.findUserById(-1);
    }

    @Test
    public void findUserById_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserById(userProfileDto.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void findUserProfileByEmail_nullEmail() {
        userProfileService.findUserProfileByEmail(null);
    }

    @Test(expected = UserProfileException.class)
    public void findUserProfileByEmail_notFound() {
        userProfileService.findUserProfileByEmail(UUID.randomUUID().toString());
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

    @Test(expected = NullPointerException.class)
    public void loadUserByUsername_nullEmail() {
        userProfileService.loadUserByUsername(null);
    }

    @Test(expected = UserProfileException.class)
    public void loadUserByUsername_notFound() {
        userProfileService.loadUserByUsername(UUID.randomUUID().toString());
    }

    @Test
    public void loadUserByUsername_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                new AseUserDetails(userProfileDto),
                userProfileService.loadUserByUsername(userProfileDto.getEmail()));
    }

}
