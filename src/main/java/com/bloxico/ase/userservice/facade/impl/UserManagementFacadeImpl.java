package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.user.SearchUsersRequest;
import com.bloxico.ase.userservice.web.model.user.SearchUsersResponse;
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
    public SearchUsersResponse searchUsers(SearchUsersRequest request, PageRequest page) {
        log.info("UserManagementFacadeImpl.searchUsers - start | request: {}, page: {}, page", request, page);
        var result = userService.findUsersByEmailOrRole(request, page);
        var response = new SearchUsersResponse(result);
        log.info("UserManagementFacadeImpl.searchUsers - end | request: {}, page: {}, page", request, page);
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
