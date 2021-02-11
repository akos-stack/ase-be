package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.user.Role.ADMIN;
import static java.lang.Integer.MAX_VALUE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserManagementFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilToken utilToken;
    @Autowired private UtilUser utilUser;
    @Autowired private TokenBlacklistServiceImpl tokenBlacklistService;
    @Autowired private UserServiceImpl userService;
    @Autowired private UserManagementFacadeImpl userManagementFacade;

    // TODO-TEST searchUsers_nullArgs

    // TODO-TEST searchUsers_notFound

    @Test
    public void searchUsers_byEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedUserDto();
        var u3 = utilUser.savedUserDto();
        assertThat(
                userManagementFacade.searchUsers(u1.getEmail(), null, 0, MAX_VALUE, "name").getUsers(),
                allOf(hasItems(u1), not(hasItems(u2, u3))));
    }

    @Test
    public void searchUsers_byRole() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedAdminDto();
        var u3 = utilUser.savedAdminDto();
        assertThat(
                userManagementFacade.searchUsers("", ADMIN, 0, MAX_VALUE, "name").getUsers(),
                allOf(hasItems(u2, u3), not(hasItems(u1))));
    }

    @Test
    public void searchUsers_byRoleAndEmail() {
        var u1 = utilUser.savedUserDto();
        var u2 = utilUser.savedAdminDto();
        var u3 = utilUser.savedAdminDto();
        assertThat(
                userManagementFacade.searchUsers(u3.getEmail(), ADMIN, 0, MAX_VALUE, "name").getUsers(),
                allOf(hasItems(u3), not(hasItems(u1, u2))));
    }

    @Test
    public void disableUser_notFound() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                UserException.class,
                () -> userManagementFacade.disableUser(-1, principalId));
    }

    @Test
    public void disableUser() {
        var principalId = utilUser.savedAdmin().getId();
        var user = utilUser.savedUser();
        var userId = user.getId();
        var t1 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        var t2 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        var t3 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        assertThat(
                tokenBlacklistService.blacklistedTokens(),
                not(hasItems(t1, t2, t3)));
        assertTrue(userService.findUserById(userId).getEnabled());
        userManagementFacade.disableUser(userId, principalId);
        assertThat(
                tokenBlacklistService.blacklistedTokens(),
                hasItems(t1, t2, t3));
        assertFalse(userService.findUserById(userId).getEnabled());
    }

    @Test
    public void blacklistTokens_notFound() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                UserException.class,
                () -> userManagementFacade.blacklistTokens(-1, principalId));
    }

    @Test
    public void blacklistTokens() {
        var principalId = utilUser.savedAdmin().getId();
        var user = utilUser.savedUser();
        var userId = user.getId();
        var t1 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        var t2 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        var t3 = utilToken.savedOauthToken(user.getEmail()).getTokenId();
        assertThat(
                tokenBlacklistService.blacklistedTokens(),
                not(hasItems(t1, t2, t3)));
        assertTrue(userService.findUserById(userId).getEnabled());
        userManagementFacade.blacklistTokens(userId, principalId);
        assertThat(
                tokenBlacklistService.blacklistedTokens(),
                hasItems(t1, t2, t3));
        assertTrue(userService.findUserById(userId).getEnabled());
    }

}
