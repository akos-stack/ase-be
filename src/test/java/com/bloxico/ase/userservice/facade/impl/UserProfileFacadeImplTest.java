package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProfileFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileFacadeImpl userProfileFacade;

    @Test
    public void returnMyProfileData_userNotFound() {
        assertThrows(
                UserException.class,
                () -> userProfileFacade.returnMyProfileData(-1));
    }

    @Test
    public void returnMyProfileData() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var response = userProfileFacade.returnMyProfileData(userProfileDto.getUserId());
        assertEquals(userProfileDto, response.getUserProfile());
    }

    @Test
    public void updateMyProfile_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> userProfileFacade.updateMyProfile(1, null));
    }

    @Test
    public void updateMyProfile_notFound() {
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        assertThrows(
                UserException.class,
                () -> userProfileFacade.updateMyProfile(-1, request));
    }

    @Test
    public void updateMyProfile() {
        var id = mockUtil.savedUserProfile().getUserId();
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        var response = userProfileFacade.updateMyProfile(id, request);
        assertEquals(request.getFirstName(), response.getUserProfile().getFirstName());
        assertEquals(request.getLastName(), response.getUserProfile().getLastName());
        assertEquals(request.getPhone(), response.getUserProfile().getPhone());
    }

}
