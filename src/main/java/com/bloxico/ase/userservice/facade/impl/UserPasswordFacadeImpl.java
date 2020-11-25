package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserPasswordFacade;
import com.bloxico.ase.userservice.service.user.IUserPasswordService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.password.ForgottenPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.KnownPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserPasswordFacadeImpl implements IUserPasswordFacade {

    private final IUserProfileService userProfileService;
    private final IUserPasswordService userPasswordService;
    private final ITokenService<PasswordResetToken> passwordResetTokenService;
    private final MailUtil mailUtil;

    @Autowired
    public UserPasswordFacadeImpl(IUserProfileService userProfileService,
                                  IUserPasswordService userPasswordService,
                                  ITokenService<PasswordResetToken> passwordResetTokenService,
                                  MailUtil mailUtil)
    {
        this.userProfileService = userProfileService;
        this.userPasswordService = userPasswordService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailUtil = mailUtil;
    }

    @Override
    public void handleForgotPasswordRequest(ForgotPasswordRequest request) {
        log.info("UserPasswordFacadeImpl.handleForgotPasswordRequest - start | request: {}", request);
        var email = request.getEmail();
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        var token = passwordResetTokenService.createNewOrReturnNonExpiredTokenForUser(userId);
        mailUtil.sendResetPasswordTokenEmail(email, token.getTokenValue());
        log.info("UserPasswordFacadeImpl.handleForgotPasswordRequest - end | request: {}", request);
    }

    @Override
    public void resendPasswordToken(ResendTokenRequest request) {
        log.info("UserPasswordFacadeImpl.resendPasswordToken - start | request: {}", request);
        var email = request.getEmail();
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        var token = passwordResetTokenService.getTokenByUserId(userId);
        mailUtil.sendResetPasswordTokenEmail(email, token.getTokenValue());
        log.info("UserPasswordFacadeImpl.resendPasswordToken - end | request: {}", request);
    }

    @Override
    public void updateForgottenPassword(ForgottenPasswordUpdateRequest request) {
        log.info("UserPasswordFacadeImpl.updateForgottenPassword - start | request: {}", request);
        var email = request.getEmail();
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        passwordResetTokenService.consumeTokenForUser(userId, request.getTokenValue());
        userPasswordService.updateForgottenPassword(userId, request.getNewPassword());
        log.info("UserPasswordFacadeImpl.updateForgottenPassword - end | request: {}", request);
    }

    @Override
    public void updateKnownPassword(long principalId, KnownPasswordUpdateRequest request) {
        log.info("UserPasswordFacadeImpl.updateKnownPassword - start | principalId: {}, request: {}", principalId, request);
        userPasswordService.updateKnownPassword(principalId, request.getOldPassword(), request.getNewPassword());
        log.info("UserPasswordFacadeImpl.updateKnownPassword - end | principalId: {}, request: {}", principalId, request);
    }

    @Override
    public void setNewPassword(long principalId, SetPasswordRequest request) {
        log.info("UserPasswordFacadeImpl.setNewPassword - start | principalId: {}, request: {}", principalId, request);
        userPasswordService.setNewPassword(principalId, request.getPassword());
        log.info("UserPasswordFacadeImpl.setNewPassword - end | principalId: {}, request: {}", principalId, request);
    }

}
