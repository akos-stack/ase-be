package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.facade.IUserProfileFacade;
import com.bloxico.userservice.services.user.IUserProfileService;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityNotFoundException;

public class UserProfileFacadeImplTest extends AbstractUnitTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserProfileFacade userProfileFacade;

    @Autowired
    private IUserProfileService userProfileService;

    private String addressHash;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }


    @Test
    @DirtiesContext
    public void givenValidEmail_whenGettingMyProfile_thenGetMyProfile() {

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;

        //when
        UserProfileDataResponse dataResponse = userProfileFacade.returnMyProfileData(email);

        //then
        Assert.assertEquals(dataResponse.getUserProfile().getEmail(), email);
        Assert.assertFalse(dataResponse.getRegions().isEmpty());
    }

    @Test
    @DirtiesContext
    public void givenInvalidEmail_whenGettingMyProfile_thenThrowError() {

        //given
        String email = "unknown@email.com";

        //when
        try {
            userProfileFacade.returnMyProfileData(email);
            Assert.fail();
        } catch (CoinUserException e) {
            Assert.assertEquals(ErrorCodes.USER_PROFILE_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }


    @Test
    @DirtiesContext
    public void givenValidMailAndDto_whenUpdatingProfile_thenUpdateProfile() {

        CoinUserDto coinUserDto = userService.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "Croatia";

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCity(newCity);
        updateProfileRequest.setName(newName);
        updateProfileRequest.setRegion(newRegion);

        //when
        userProfileFacade.updateMyProfile(email, updateProfileRequest);


        //then
        UserProfileDto updatedProfile = userProfileService.findProfileByEmail(email);

        Assert.assertEquals(updatedProfile.getCity(), newCity);
        Assert.assertEquals(updatedProfile.getName(), newName);
        Assert.assertEquals(updatedProfile.getRegion(), newRegion);
    }

    @Test
    @DirtiesContext
    public void givenInvalidMail_whenUpdatingProfile_thenThrowProfileNotFoundError() {

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "Croatia";


        //given
        String email = "unknown@eemail.com";
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCity(newCity);
        updateProfileRequest.setName(newName);
        updateProfileRequest.setRegion(newRegion);

        //when
        try {
            userProfileFacade.updateMyProfile(email, updateProfileRequest);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_PROFILE_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenInvalidRegionInDto_whenUpdatingProfile_thenThrowProfileNotFoundError() {

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "Unknown";

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setCity(newCity);
        updateProfileRequest.setName(newName);
        updateProfileRequest.setRegion(newRegion);

        //when
        try {
            userProfileFacade.updateMyProfile(email, updateProfileRequest);
            Assert.fail();
        } catch (EntityNotFoundException e) {
            //then
            Assert.assertEquals(ErrorCodes.REGION_NOT_FOUND.getCode(), e.getMessage());
        }
    }
}