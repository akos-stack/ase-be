package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;

public class UserProfileFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileFacadeImpl userProfileFacade;

    @Test(expected = UserException.class)
    public void returnMyProfileData_userNotFound() {
        userProfileFacade.returnMyProfileData(-1);
    }

    @Test
    public void returnMyProfileData() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var userProfileDataResponse = userProfileFacade.returnMyProfileData(userProfileDto.getUserId());
        assertEquals(userProfileDto, userProfileDataResponse.getUserProfile());
    }

    @Test(expected = NullPointerException.class)
    public void updateMyProfile_nullRequest() {
        userProfileFacade.updateMyProfile(1, null);
    }

    @Test(expected = UserException.class)
    public void updateMyProfile_notFound() {
        var request = new UpdateUserProfileRequest(uuid(), uuid(), uuid());
        userProfileFacade.updateMyProfile(-1, request);
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
