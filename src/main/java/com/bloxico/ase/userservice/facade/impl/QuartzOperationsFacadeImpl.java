package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IQuartzOperationsFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.PasswordResetTokenServiceImpl;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class QuartzOperationsFacadeImpl implements IQuartzOperationsFacade {

    private final ITokenService registrationTokenService;
    private final ITokenService passwordResetTokenService;
    private final IOAuthAccessTokenService accessTokenService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final IUserService userService;

    @Autowired
    public QuartzOperationsFacadeImpl(RegistrationTokenServiceImpl registrationTokenService,
                                      PasswordResetTokenServiceImpl passwordResetTokenService,
                                      IOAuthAccessTokenService accessTokenService,
                                      ITokenBlacklistService tokenBlacklistService,
                                      IUserService userService)
    {
        this.registrationTokenService = registrationTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.accessTokenService = accessTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
    }

    @Override
    public void deleteExpiredTokens() {
        log.info("QuartzFacadeImpl.deleteExpiredTokens - start");
        var userIds = registrationTokenService.deleteExpiredTokens();
        userService.deleteDisabledUsersWithIds(userIds);
        passwordResetTokenService.deleteExpiredTokens();
        accessTokenService.deleteExpiredTokens();
        tokenBlacklistService.deleteExpiredTokens();
        log.info("QuartzFacadeImpl.deleteExpiredTokens - end");
    }

}
