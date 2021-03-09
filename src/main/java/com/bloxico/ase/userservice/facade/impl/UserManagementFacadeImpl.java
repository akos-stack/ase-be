package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.web.model.user.PagedUserDataResponse;
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
    public PagedUserDataResponse searchUsers(String email, String role, int page, int size, String sort) {
        log.info("UserManagementFacadeImpl.searchUsers - start | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        var userDtos = userService.findUsersByEmailOrRole(email, role, page, size, sort);
        var response = new PagedUserDataResponse(userDtos.getContent(), userDtos.getContent().size(), userDtos.getTotalElements(), userDtos.getTotalPages());
        log.info("UserManagementFacadeImpl.searchUsers - end | email: {}, role: {}, page: {}, size: {}, sort: {}", email, role, page, size, sort);
        return response;
    }

    @Override
    public void disableUser(long userId) {
        log.info("UserManagementFacadeImpl.disableUser - start | userId: {}", userId);
        userService.disableUser(userId);
        blacklistTokens(userId);
        log.info("UserManagementFacadeImpl.disableUser - end | userId: {}", userId);
    }

    @Override
    public void blacklistTokens(long userId) {
        log.info("UserManagementFacadeImpl.blacklistTokens - start | userId: {}", userId);
        var email = userService.findUserById(userId).getEmail();
        var tokens = oAuthAccessTokenService.deleteTokensByEmail(email);
        tokenBlacklistService.blacklistTokens(tokens);
        log.info("UserManagementFacadeImpl.blacklistTokens - end | userId: {}", userId);
    }

}
