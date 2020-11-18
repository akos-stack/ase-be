package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserRegistrationFacade;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.service.user.IUserRegistrationService;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationResponse;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserRegistrationFacadeImpl implements IUserRegistrationFacade {

    private final IUserProfileService userProfileService;
    private final IUserRegistrationService userRegistrationService;
    private final ITokenService<VerificationToken> verificationTokenService;
    private final MailUtil mailUtil;

    @Autowired
    public UserRegistrationFacadeImpl(IUserProfileService userProfileService,
                                      IUserRegistrationService userRegistrationService,
                                      ITokenService<VerificationToken> verificationTokenService,
                                      MailUtil mailUtil)
    {
        this.userProfileService = userProfileService;
        this.userRegistrationService = userRegistrationService;
        this.verificationTokenService = verificationTokenService;
        this.mailUtil = mailUtil;
    }

    @Override
    public RegistrationResponse registerUserWithVerificationToken(RegistrationRequest request) {
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - start | request: {}", request);
        var userProfileDto = userRegistrationService.registerDisabledUser(request);
        var tokenDto = verificationTokenService.createTokenForUser(userProfileDto.getId());
        mailUtil.sendVerificationTokenEmail(userProfileDto.getEmail(), tokenDto.getTokenValue());
        var response = new RegistrationResponse(tokenDto.getTokenValue());
        log.info("UserRegistrationFacadeImpl.registerUserWithVerificationToken - end | request: {}", request);
        return response;
    }

    @Override
    public void handleTokenValidation(TokenValidationRequest request) {
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - start | request: {}", request);
        var userProfileDto = userProfileService.findUserProfileByEmail(request.getEmail());
        verificationTokenService.consumeTokenForUser(userProfileDto.getId(), request.getTokenValue());
        userRegistrationService.enableUser(userProfileDto.getId());
        log.info("UserRegistrationFacadeImpl.handleTokenValidation - end | request: {}", request);
    }

    @Override
    public void refreshExpiredToken(String expiredTokenValue) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | expiredTokenValue: {}", expiredTokenValue);
        var tokenDto = verificationTokenService.refreshExpiredToken(expiredTokenValue);
        var userProfileDto = userProfileService.findUserById(tokenDto.getId());
        mailUtil.sendVerificationTokenEmail(userProfileDto.getEmail(), tokenDto.getTokenValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | expiredTokenValue: {}", expiredTokenValue);
    }

    @Override
    public void resendVerificationToken(ResendTokenRequest request) {
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - start | request: {}", request);
        var userProfileDto = userProfileService.findUserProfileByEmail(request.getEmail());
        var tokenDto = verificationTokenService.getTokenByUserId(userProfileDto.getId());
        mailUtil.sendVerificationTokenEmail(request.getEmail(), tokenDto.getTokenValue());
        log.info("UserRegistrationFacadeImpl.refreshExpiredToken - end | request: {}", request);
    }

}
