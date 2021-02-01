package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
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
    private UserServiceImpl userService;

    @Autowired
    private UserManagementFacadeImpl userManagementFacade;


    @Test
    public void searchUsersByEmail() {
        mockUtil.saveUsers();
        assertEquals(1, userManagementFacade.searchUsers("user1", null, 0, 10, "name").getUsers().size());
    }

    @Test
    public void searchUsersByRole() {
        mockUtil.saveUsers();
        assertEquals(4, userManagementFacade.searchUsers("", "admin", 0, 10, "name").getUsers().size());
    }

    @Test
    public void searchUsersByRoleAndEmail() {
        mockUtil.saveUsers();
        assertEquals(3, userManagementFacade.searchUsers("user", "admin", 0, 10, "name").getUsers().size());
    }

    @Test(expected = UserException.class)
    public void disableUser_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userManagementFacade.disableUser(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void disableUser() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUser();
        var userId = user.getId();
        var tokens = Set.of(
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userService.findUserById(userId).getEnabled());
        userManagementFacade.disableUser(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertFalse(userService.findUserById(userId).getEnabled());
    }

    @Test(expected = UserException.class)
    public void blacklistTokens_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userManagementFacade.blacklistTokens(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void blacklistTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUser();
        var userId = user.getId();
        var tokens = Set.of(
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId(),
                mockUtil.savedOauthToken(user.getEmail()).getTokenId());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userService.findUserById(userId).getEnabled());
        userManagementFacade.blacklistTokens(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertTrue(userService.findUserById(userId).getEnabled());
    }

}
