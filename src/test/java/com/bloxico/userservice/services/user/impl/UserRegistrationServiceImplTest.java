package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.RegistrationRequestDto;
import com.bloxico.userservice.entities.user.CoinRole;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.CoinUserRole;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.UserRoleRepository;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.util.mappers.RegistrationRequestMapper;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public class UserRegistrationServiceImplTest extends AbstractUnitTest {

    @Autowired
    private IUserRegistrationService userRegistrationService;

    @Autowired
    private ITokenService<VerificationToken> verificationTokenService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    @Transactional
    public void givenValidRegistrationRequest_whenRegisteringDisabledUser_RegisterDisabledUser() {

        RegistrationRequest registrationRequest = mockUtil.createMockRegistrationRequest("newUser@noreply.com");

        //given
        RegistrationRequestDto registrationRequestDto = RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest);

        //when
        userRegistrationService.registerDisabledUser(registrationRequestDto);

        //then
        CoinUser newUser = mockUtil.findUserByEmail("newUser@noreply.com");
        Assert.assertEquals(registrationRequestDto.getEmail(), newUser.getEmail());

        Assert.assertEquals(newUser.getUserProfile().getRegion().getRegionName(), registrationRequest.getRegionName());

        Assert.assertFalse(newUser.isEnabled());

        Assert.assertTrue(newUser.getCoinUserRoles().contains(new CoinUserRole(newUser, new CoinRole(CoinRole.RoleName.USER))));
    }

    @Test
    @DirtiesContext
    public void givenInvalidRegionNameInRegistrationRequest_whenRegisteringDisabledUserHalfwayThroughTransaction_thenRollbackInsertedData() {

        RegistrationRequest registrationRequest = mockUtil.createMockRegistrationRequest("newUser@noreply.com");

        //given
        RegistrationRequestDto registrationRequestDto = RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest);
        registrationRequestDto.setRegionName("nonExistingRegion");

        //when
        try {
            userRegistrationService.registerDisabledUser(registrationRequestDto);
        } catch (EntityNotFoundException e) {
        }

        try {
            CoinUser newUser = mockUtil.findUserByEmail("newUser@noreply.com");
            Assert.fail();

        } catch (CoinUserException e) {
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenInvalidMatchingPasswordInRegistrationRequest_whenRegisteringDisabledUser_thenReturnCoinUserException() {

        RegistrationRequest registrationRequest = mockUtil.createMockRegistrationRequest("newUser@noreply.com");

        //given
        RegistrationRequestDto registrationRequestDto = RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest);
        registrationRequestDto.setMatchPassword("not equal to password");

        //when
        try {
            userRegistrationService.registerDisabledUser(registrationRequestDto);
            Assert.fail();
        } catch (CoinUserException e) {
            Assert.assertEquals(ErrorCodes.MATCH_REGISTRATION_PASSWORD_ERROR.getCode(), e.getMessage());
        }
    }


    @Test
    @DirtiesContext
    public void givenExistingRegistrationRequest_whenRegisteringDisabledUser_ThrowUserExistsError() {

        RegistrationRequest registrationRequest = mockUtil.createMockRegistrationRequest(MockUtil.Constants.MOCK_USER_EMAIL);

        //given
        RegistrationRequestDto registrationRequestDto = RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest);


        //when
        try {
            userRegistrationService.registerDisabledUser(registrationRequestDto);
            Assert.fail();
        } catch (CoinUserException e) {
            Assert.assertEquals(ErrorCodes.USER_EXISTS.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenCoinUser_whenEnablingUser_thenEnableUser() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        userRegistrationService.enableUser(coinUser.getId());


        //then
        CoinUser enabledUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(enabledUser.isEnabled());

        Optional<CoinUserRole> userRole = userRoleRepository.findByCoinUserId(coinUser.getId());

        Assert.assertTrue(userRole.isPresent());

    }


    @Test
    @DirtiesContext
    public void whenDeletingDisabledCoinUsersWithoutVerificationToken_thenDeleteUser() {

        CoinUser mockUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        List<Long> userIds = verificationTokenService.deleteExpiredTokens();

        //when
        userRegistrationService.deleteDisabledUsersWithIds(userIds);

        try {
            mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());

            Optional<CoinUserRole> userRole = userRoleRepository.findByCoinUserId(mockUser.getId());

            Assert.assertFalse(userRole.isPresent());
        }
    }

    @Test
    @DirtiesContext
    public void whenDeletingEnabledCoinUsersWithoutVerificationToken_thenDoNotDeleteUser() {

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);
        List<Long> userIds = verificationTokenService.deleteExpiredTokens();

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);


        userRegistrationService.enableUser(coinUser.getId());


        //when
        userRegistrationService.deleteDisabledUsersWithIds(userIds);


        CoinUser refreshedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Assert.assertTrue(refreshedCoinUser.isEnabled());
    }
}
