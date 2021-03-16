package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProfileFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UserProfileFacadeImpl userProfileFacade;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    public void returnMyProfileData_userNotFound() {
        assertThrows(
                NullPointerException.class,
                () -> userProfileFacade.returnMyProfileData());
    }

    @Test
    @WithMockCustomUser(role = Role.USER)
    public void returnMyProfileData() {
        var userProfileDto = utilUserProfile.savedUserProfileDto(securityContext.getLoggedInUserId());
        var response = userProfileFacade.returnMyProfileData();
        assertEquals(userProfileDto, response.getUserProfile());
    }

    @Test
    @WithMockCustomUser
    public void updateMyProfile_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userProfileFacade.updateMyProfile(null));
    }

    @Test
    public void updateMyProfile_notFound() {
        var request = new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID());
        assertThrows(
                NullPointerException.class,
                () -> userProfileFacade.updateMyProfile(request));
    }

    @Test
    @WithMockCustomUser(role = Role.USER)
    public void updateMyProfile() {
        var userProfileDto = utilUserProfile.savedUserProfileDto(securityContext.getLoggedInUserId());
        var request = new UpdateUserProfileRequest(genUUID(), genUUID(), genUUID());
        var response = userProfileFacade.updateMyProfile(request);
        assertEquals(request.getFirstName(), response.getUserProfile().getFirstName());
        assertEquals(request.getLastName(), response.getUserProfile().getLastName());
        assertEquals(request.getPhone(), response.getUserProfile().getPhone());
    }

}
