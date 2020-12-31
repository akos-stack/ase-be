package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserManagementFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private UserManagementFacadeImpl userManagementFacade;


    @Test
    public void searchUsers() {
        mockUtil.saveUserProfiles();
        assertTrue(userManagementFacade.searchUsers("user1", null, 0, 10, "name").getUserProfiles().size() == 1);
    }

    @Test(expected = UserProfileException.class)
    public void disableUser_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userManagementFacade.disableUser(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void disableUser() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUserProfile();
        var userId = user.getId();
        var tokens = Set.of(
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userManagementFacade.disableUser(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertFalse(userProfileService.findUserProfileById(userId).getEnabled());
    }

    @Test(expected = UserProfileException.class)
    public void blacklistTokens_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userManagementFacade.blacklistTokens(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void blacklistTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUserProfile();
        var userId = user.getId();
        var tokens = Set.of(
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userManagementFacade.blacklistTokens(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
    }

}
