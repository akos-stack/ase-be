package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.UserRole;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.facade.IDeleteTokenQuartzFacade;
import com.bloxico.userservice.repository.user.UserRoleRepository;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

public class DeleteTokenQuartzFacadeImplTest extends AbstractUnitTest {

    @Autowired
    private IDeleteTokenQuartzFacade deleteTokenQuartzService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private IUserRegistrationService userRegistrationService;

    private final String MOCK_PARTNER_USER_ID = "d2uswaluter";

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void whenDeletingDisabledCoinUsersWithoutVerificationToken_thenDeleteUser() {

        CoinUser mockUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        deleteTokenQuartzService.performTokenDelete();

        try {
            mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());

            Optional<UserRole> userRole = userRoleRepository.findByCoinUserId(mockUser.getId());

            Assert.assertFalse(userRole.isPresent());
        }
    }

    @Test
    @DirtiesContext
    public void whenDeletingEnabledCoinUsersWithoutVerificationToken_thenDoNotDeleteUser() {

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);


        userRegistrationService.enableUser(coinUser.getId());


        //when
        deleteTokenQuartzService.performTokenDelete();


        CoinUser refreshedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        Assert.assertTrue(refreshedCoinUser.isEnabled());
    }

    @Test
    @DirtiesContext
    public void whenDeletingExternalDisabledCoinUsersWithoutVerificationToken_thenDeleteUserAndPairAndTransactions() {

        CoinUser mockUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        deleteTokenQuartzService.performTokenDelete();

        try {
            mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());

            Optional<UserRole> userRole = userRoleRepository.findByCoinUserId(mockUser.getId());

            Assert.assertFalse(userRole.isPresent());
        }
    }

    @Test
    @DirtiesContext
    public void whenDeletingExternalEnabledCoinUsersWithoutVerificationToken_thenDontDeleteUserAndHisData() {

        CoinUser mockUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        userRegistrationService.enableUser(mockUser.getId());

        mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        deleteTokenQuartzService.performTokenDelete();

        //then
        CoinUser refreshedCoinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(refreshedCoinUser.isEnabled());

    }
}