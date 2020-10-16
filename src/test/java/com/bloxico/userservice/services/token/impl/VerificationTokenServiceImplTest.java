package com.bloxico.userservice.services.token.impl;

import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.exceptions.TokenException;
import com.bloxico.userservice.repository.token.VerificationTokenRepository;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;

public class VerificationTokenServiceImplTest extends AbstractUnitTest {

    @Autowired
    private VerificationTokenServiceImpl verificationTokenService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenUser_whenCreatingTokenForUser_thenReturnToken() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        TokenDto token = verificationTokenService.createTokenForUser(coinUser.getId());

        //then
        Assert.assertNotNull(token);

    }

    @Test
    @DirtiesContext
    public void givenValidUserId_whenGettingTokenByUserId_thenReturnValidToken() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        long userId = coinUser.getId();

        //when
        TokenDto fetchedToken = verificationTokenService.getTokenByUserId(userId);

        //then
        Assert.assertEquals(fetchedToken.getUserId(), coinUser.getId());
    }

    @Test
    @DirtiesContext
    public void givenInvalidUserId_whenGettingTokenByUserId_thenReturnTokenException() {

        //given
        long userId = 12L;

        //when
        try {
            TokenDto token = verificationTokenService.getTokenByUserId(userId);
            Assert.fail();
        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void whenDeletingExpiredToken_thenDeleteToken() {

        VerificationToken token = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(token.getExpiryDate().before(new Date()));

        //when
        verificationTokenService.deleteExpiredTokens();

        try {
            CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
            TokenDto refreshedToken = verificationTokenService.getTokenByUserId(coinUser.getId());
            Assert.fail();
        } catch (TokenException e) {
            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void whenDeletingExpiredToken_thenDoNotDeleteValidTokens() {

        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto token = verificationTokenService.getTokenByUserId(coinUser.getId());

        Assert.assertTrue(token.getExpiryDate().after(new Date()));

        //when
        verificationTokenService.deleteExpiredTokens();

        coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto refreshedToken = verificationTokenService.getTokenByUserId(coinUser.getId());

        //then
        Assert.assertNotNull(refreshedToken);
    }

    @Test
    @DirtiesContext
    public void givenCoinUserWithExistingToken_whenReturningExistingToken_thenReturnExistingToken() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        TokenDto verificationToken = verificationTokenService.createNewOrReturnNonExpiredTokenForUser(coinUser.getId());

        Assert.assertNotNull(verificationToken);

        Assert.assertTrue(verificationToken.getExpiryDate().after(new Date()));
    }

    @Test
    @DirtiesContext
    public void givenCoinUserWithExpiredToken_whenReturningExistingToken_thenReturnNull() {

        VerificationToken expiredToken = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);
        Assert.assertTrue(expiredToken.getExpiryDate().before(new Date()));

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        TokenDto newToken = verificationTokenService.createNewOrReturnNonExpiredTokenForUser(coinUser.getId());

        //then
        Assert.assertNotEquals(newToken, expiredToken);

        Assert.assertTrue(newToken.getExpiryDate().after(new Date()));
    }

    @Test
    @DirtiesContext
    public void givenValidCoinUserAndTokenValue_whenConsumingToken_thenConsumeToken() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        TokenDto tokenDto = verificationTokenService.getTokenByUserId(coinUser.getId());

        //when
        verificationTokenService.consumeTokenForUser(coinUser.getId(), tokenDto.getTokenValue());

        //then
        try {
            verificationTokenService.getTokenByUserId(coinUser.getId());
        } catch (TokenException e) {
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenNonExistingTokenValue_whenConsumingToken_thenThrowTokenNotFoundError() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        String nonExistingTokenValue = "1232444";

        //when
        try {
            verificationTokenService.consumeTokenForUser(coinUser.getId(), nonExistingTokenValue);
        } catch (TokenException e) {

            //then
            Assert.assertEquals(ErrorCodes.TOKEN_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void givenExpiredTokenValue_whenConsumingToken_thenThrowTokenExpiredError() {

        //given
        CoinUser coinUser = mockUtil.findUserByEmail(MockUtil.Constants.MOCK_USER_EMAIL);
        VerificationToken expiredToken = mockUtil.expireVerificationTokenForUser(MockUtil.Constants.MOCK_USER_EMAIL);

        //when
        try {
            verificationTokenService.consumeTokenForUser(coinUser.getId(), expiredToken.getTokenValue());
        } catch (TokenException e) {

            //then
            Assert.assertEquals(ErrorCodes.TOKEN_EXPIRED.getCode(), e.getMessage());
        }
    }
}
