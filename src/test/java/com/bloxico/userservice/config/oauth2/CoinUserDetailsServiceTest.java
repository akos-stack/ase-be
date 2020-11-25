package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;

public class CoinUserDetailsServiceTest extends AbstractUnitTest {

    //@Autowired
    private CoinUserDetailsService coinUserDetailsService;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
        mockUtil.enableUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    //@Test
    @DirtiesContext
    public void givenUpperCasedEmail_whenLoadingUserByUserName_thenReturnValidCoinUserEntity() {

        //given
        String upperCasedEmail = MockUtil.Constants.MOCK_USER_EMAIL.toUpperCase();

        //when
        UserDetails userDetails = coinUserDetailsService.loadUserByUsername(upperCasedEmail);

        //then
        Assert.assertEquals(MockUtil.Constants.MOCK_USER_EMAIL, userDetails.getUsername());
    }
}