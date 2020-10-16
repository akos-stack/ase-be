package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.dto.UpdatePasswordDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.services.user.IUserPasswordService;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

public class UserPasswordServiceImplTest extends AbstractUnitTest {

    @Autowired
    private IUserRegistrationService userRegistrationService;

    @Autowired
    private IUserPasswordService userPasswordService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenValidUserAndForgotPasswordDto_whenUpdatingForgottenPassword_thenUpdateForgottenPassword() {

        //given
        CoinUser preUpdatedUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setNewPassword("randomNewPassword1!");

        //when
        userPasswordService.updateForgottenPassword(preUpdatedUser.getId(), forgotPasswordDto);

        //then
        CoinUser updatedUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(passwordEncoder.matches(forgotPasswordDto.getNewPassword(), updatedUser.getPassword()));

    }

    @Test
    @DirtiesContext
    public void givenValidUserPasswordRequest_whenUpdatingKnownPassword_thenUpdateKnownPassword() {

        //given
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setOldPassword(MockUtil.Constants.MOCK_USER_PASSWORD);
        updatePasswordDto.setNewPassword("newPassword1!");

        //when
        userPasswordService.updateKnownPassword(MockUtil.Constants.MOCK_USER_EMAIL, updatePasswordDto);

        //then
        CoinUser updatedUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(passwordEncoder.matches(updatePasswordDto.getNewPassword(), updatedUser.getPassword()));
    }

    @Test
    @DirtiesContext
    public void givenInvalidOldPassword_whenUpdatingKnownPassword_thenReturnPasswordsDontMatchError() {

        //given
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto();
        updatePasswordDto.setOldPassword("notCorrectPassword1!");
        updatePasswordDto.setNewPassword("newPassword1!");

        //when
        try {
            userPasswordService.updateKnownPassword(MockUtil.Constants.MOCK_USER_EMAIL, updatePasswordDto);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.OLD_PASSWORD_DOES_NOT_MATCH.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenValidPasswordAndEmail_whenSettingNewPassword_thenSetNewPassword() {

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;
        String password = "newPassword123!";

        //when
        userPasswordService.setNewPassword(email, password);

        //then
        CoinUser updatedUser = mockUtil.findUserByEmail(email);
        Assert.assertTrue(passwordEncoder.matches(password, updatedUser.getPassword()));
    }

    @Test
    @DirtiesContext
    public void givenNonExistingEmail_whenSettingNewPassword_thenThrowUserDoesNotExistException() {

        //given
        String email = "nonexisting@noreply.com";
        String password = "newPassword123!";

        //when
        try {
            userPasswordService.setNewPassword(email, password);
        } catch (CoinUserException e) {

            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

}
