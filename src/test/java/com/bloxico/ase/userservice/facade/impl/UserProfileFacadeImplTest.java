package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

public class UserProfileFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private UserProfileFacadeImpl userProfileFacade;

    @Test(expected = UserProfileException.class)
    public void returnMyProfileData_notFound() {
        userProfileFacade.returnMyProfileData(-1);
    }

    @Test
    public void returnMyProfileData() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        var userProfileDataResponse = userProfileFacade.returnMyProfileData(userProfileDto.getId());
        assertEquals(userProfileDto, userProfileDataResponse.getUserProfile());
    }

    @Test(expected = NullPointerException.class)
    public void updateMyProfile_nullRequest() {
        userProfileFacade.updateMyProfile(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateMyProfile_notFound() {
        var request = new UpdateUserProfileRequest("John", "007008123");
        userProfileFacade.updateMyProfile(-1, request);
    }

    @Test
    public void updateMyProfile() {
        var id = mockUtil.savedUserProfile().getId();
        var request = new UpdateUserProfileRequest("John", "007008123");
        var response = userProfileFacade.updateMyProfile(id, request);
        assertEquals(request.getName(), response.getUserProfile().getName());
        assertEquals(request.getPhone(), response.getUserProfile().getPhone());
    }

    @Test(expected = UserProfileException.class)
    public void disableUser_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userProfileFacade.disableUser(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void disableUser() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUserProfileDto();
        var userId = user.getId();
        var tokens = mockUtil
                .genSavedTokens(3, user.getEmail())
                .stream()
                .map(OAuthAccessToken::getTokenId)
                .collect(toSet());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userProfileFacade.disableUser(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertFalse(userProfileService.findUserProfileById(userId).getEnabled());
    }

    @Test(expected = UserProfileException.class)
    public void blacklistTokens_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        userProfileFacade.blacklistTokens(-1, principalId);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD) // clear cache
    public void blacklistTokens() {
        var principalId = mockUtil.savedAdmin().getId();
        var user = mockUtil.savedUserProfileDto();
        var userId = user.getId();
        var tokens = mockUtil
                .genSavedTokens(3, user.getEmail())
                .stream()
                .map(OAuthAccessToken::getTokenId)
                .collect(toSet());
        assertEquals(Set.of(), tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userProfileFacade.blacklistTokens(userId, principalId);
        assertEquals(tokens, tokenBlacklistService.blacklistedTokens());
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
    }

}
