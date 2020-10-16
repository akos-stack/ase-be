package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.RegionDto;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.repository.user.UserProfileRepository;
import com.bloxico.userservice.repository.user.UserRoleRepository;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.test.base.AbstractUnitTest;
import com.bloxico.userservice.util.MockUtil;
import com.bloxico.userservice.web.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

public class UserServiceImplTest extends AbstractUnitTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private CoinUserRepository coinUserRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Before
    public void init() {
        mockUtil.insertMockUser(MockUtil.Constants.MOCK_USER_EMAIL);
    }

    @Test
    @DirtiesContext
    public void givenValidUserEmail_whenFindingUserByEmail_thenReturnValidUser() {

        //given
        String email = MockUtil.Constants.MOCK_USER_EMAIL;

        //when
        CoinUserDto coinUser = userService.findUserByEmail(email);

        //then
        Assert.assertEquals(email, coinUser.getEmail());

    }

    @Test
    @DirtiesContext
    public void givenInvalidUserEmail_whenFindingUserByEmail_thenReturnUserNotFoundMessage() {

        //given
        String email = "random@email.com";

        //when
        try {
            userService.findUserByEmail(email);
            Assert.fail();
        } catch (CoinUserException e) {
            //then
            Assert.assertEquals(ErrorCodes.USER_DOES_NOT_EXIST.getCode(), e.getMessage());
        }
    }

    @Test
    @DirtiesContext
    public void whenReturningRegionList_thenReturnRegions() {

        //when
        List<RegionDto> regions = userService.getRegionList();

        //then
        Assert.assertFalse(regions.isEmpty());
    }
}
