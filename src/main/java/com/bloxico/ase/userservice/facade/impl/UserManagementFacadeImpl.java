package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserManagementFacadeImpl implements IUserManagementFacade {

    private final IUserProfileService userProfileService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final IOAuthAccessTokenService oAuthAccessTokenService;

    @Autowired
    public UserManagementFacadeImpl(IUserProfileService userProfileService, ITokenBlacklistService tokenBlacklistService, IOAuthAccessTokenService oAuthAccessTokenService) {
        this.userProfileService = userProfileService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.oAuthAccessTokenService = oAuthAccessTokenService;
    }

    @Override
    public ArrayUserProfileDataResponse searchUsers(String email, String role, int page, int size, String sort) {
        log.info("UserSearchFacadeImpl.searchUsers - start | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        var userProfileDtos = userProfileService.findUsersByEmailOrRole(email, role, page, size, sort);
        var response = new ArrayUserProfileDataResponse(userProfileDtos, userProfileDtos.size());
        log.info("UserSearchFacadeImpl.searchUsers - end | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        return response;
    }

    @Override
    public void disableUser(long userId, long principalId) {
        log.info("UserProfileFacadeImpl.disableUser - start | userId: {}, principalId: {}", userId, principalId);
        userProfileService.disableUser(userId, principalId);
        blacklistTokens(userId, principalId);
        log.info("UserProfileFacadeImpl.disableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public void blacklistTokens(long userId, long principalId) {
        log.info("UserProfileFacadeImpl.blacklistTokens - start | userId: {}, principalId: {}", userId, principalId);
        var email = userProfileService.findUserProfileById(userId).getEmail();
        var tokens = oAuthAccessTokenService.deleteTokensByEmail(email);
        tokenBlacklistService.blacklistTokens(tokens, principalId);
        log.info("UserProfileFacadeImpl.blacklistTokens - end | userId: {}, principalId: {}", userId, principalId);
    }
}
