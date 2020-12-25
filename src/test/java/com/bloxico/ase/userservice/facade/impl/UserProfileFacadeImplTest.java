package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class UserProfileFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileFacadeImpl userProfileFacade;

    @Test(expected = UserProfileException.class)
    public void returnMyProfileData_notFound() {
        userProfileFacade.returnMyProfileData(-1);
    }

    @Test
    public void returnMyProfileData() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var userProfileDataResponse = userProfileFacade.returnMyProfileData(userProfileDto.getId());
        assertEquals(userProfileDto, userProfileDataResponse.getUserProfile());
    }

    @Test(expected = NullPointerException.class)
    public void updateMyProfile_nullRequest() {
        userProfileFacade.updateMyProfile(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateMyProfile_notFound() {
        var request = new UpdateUserProfileRequest("John", "007008123");
        userProfileFacade.updateMyProfile(-1, request);
    }

    @Test
    public void updateMyProfile() {
        var id = mockUtil.savedUserProfile().getId();
        var request = new UpdateUserProfileRequest("John", "007008123");
        var response = userProfileFacade.updateMyProfile(id, request);
        assertEquals(request.getName(), response.getUserProfile().getName());
        assertEquals(request.getPhone(), response.getUserProfile().getPhone());
    }

}
