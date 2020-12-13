package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IQuartzOperationsFacade;
import com.bloxico.ase.userservice.service.oauth.IOAuthAccessTokenService;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class QuartzOperationsFacadeImpl implements IQuartzOperationsFacade {

    private final ITokenService tokenService;
    private final IOAuthAccessTokenService accessTokenService;
    private final ITokenBlacklistService tokenBlacklistService;

    @Autowired
    public QuartzOperationsFacadeImpl(RegistrationTokenServiceImpl tokenService,
                                      IOAuthAccessTokenService accessTokenService,
                                      ITokenBlacklistService tokenBlacklistService)
    {
        // ITokenService impl doesn't matter
        // all expired tokens will be deleted
        this.tokenService = tokenService;
        this.accessTokenService = accessTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void deleteExpiredTokens() {
        log.info("QuartzFacadeImpl.deleteExpiredTokens - start");
        tokenService.deleteExpiredTokens();
        accessTokenService.deleteExpiredTokens();
        tokenBlacklistService.deleteExpiredTokens();
        log.info("QuartzFacadeImpl.deleteExpiredTokens - end");
    }

}
