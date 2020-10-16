package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.UpdateProfileDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.services.user.IUserProfileService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityNotFoundException;

public class UserProfileServiceImplTest extends AbstractUnitTest {

    @Autowired
    private IUserProfileService userProfileService;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenValidEmail_whenFindingUserProfileByEmail_thenReturnValidUserProfile() {

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;

        //when
        UserProfileDto userProfile = userProfileService.findProfileByEmail(email);

        //then
        Assert.assertEquals(userProfile.getEmail(), email);

    }

    @Test
    @DirtiesContext
    public void givenInvalidEmail_whenFindingUserProfileByEmail_thenReturnUserProfileNotFound() {

        //given
        String email = "unknown@email.com";

        //when
        try {
            UserProfileDto userProfile = userProfileService.findProfileByEmail(email);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_PROFILE_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenValidMailAndDto_whenUpdatingProfile_thenUpdateProfile() {

        String newCity = "Belgrade";
        String newName = "Milos";
        String newRegion = "Croatia";

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setCity(newCity);
        updateProfileDto.setName(newName);
        updateProfileDto.setRegion(newRegion);

        //when
        userProfileService.updateProfile(email, updateProfileDto);
        UserProfileDto updatedProfile = userProfileService.findProfileByEmail(email);

        //then
        Assert.assertEquals(updatedProfile.getCity(), newCity);
        Assert.assertEquals(updatedProfile.getName(), newName);
        Assert.assertEquals(updatedProfile.getRegion(), newRegion);
    }

    @Test
    @DirtiesContext
    public void givenInvalidMail_whenUpdatingProfile_thenThrowProfileNotFoundError() {

        String newCity = "Belgrade";
        String newName = "Milos";

        //given
        String email = "unknown@eemail.com";
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setCity(newCity);
        updateProfileDto.setName(newName);

        //when
        try {
            userProfileService.updateProfile(email, updateProfileDto);
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
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setCity(newCity);
        updateProfileDto.setName(newName);
        updateProfileDto.setRegion(newRegion);

        //when
        try {
            userProfileService.updateProfile(email, updateProfileDto);
            Assert.fail();
        } catch (EntityNotFoundException e) {
            //then
            Assert.assertEquals(ErrorCodes.REGION_NOT_FOUND.getCode(), e.getMessage());
        }
    }
}