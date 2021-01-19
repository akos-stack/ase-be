package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.web.model.user.ArrayUserDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserManagementFacadeImpl implements IUserManagementFacade {

    private final IUserService userService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final IOAuthAccessTokenService oAuthAccessTokenService;

    @Autowired
    public UserManagementFacadeImpl(IUserService userService,
                                    ITokenBlacklistService tokenBlacklistService,
                                    IOAuthAccessTokenService oAuthAccessTokenService)
    {
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.oAuthAccessTokenService = oAuthAccessTokenService;
    }

    @Override
    public ArrayUserDataResponse searchUsers(String email, String role, int page, int size, String sort) {
        log.info("UserManagementFacadeImpl.searchUsers - start | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        var userDtos = userService.findUsersByEmailOrRole(email, role, page, size, sort);
        var response = new ArrayUserDataResponse(userDtos, userDtos.size());
        log.info("UserManagementFacadeImpl.searchUsers - end | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        return response;
    }

    @Override
    public void disableUser(long userId, long principalId) {
        log.info("UserManagementFacadeImpl.disableUser - start | userId: {}, principalId: {}", userId, principalId);
        userService.disableUser(userId, principalId);
        blacklistTokens(userId, principalId);
        log.info("UserManagementFacadeImpl.disableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public void blacklistTokens(long userId, long principalId) {
        log.info("UserManagementFacadeImpl.blacklistTokens - start | userId: {}, principalId: {}", userId, principalId);
        var email = userService.findUserById(userId).getEmail();
        var tokens = oAuthAccessTokenService.deleteTokensByEmail(email);
        tokenBlacklistService.blacklistTokens(tokens, principalId);
        log.info("UserManagementFacadeImpl.blacklistTokens - end | userId: {}, principalId: {}", userId, principalId);
    }

}
