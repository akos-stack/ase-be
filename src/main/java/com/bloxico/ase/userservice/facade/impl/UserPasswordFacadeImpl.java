package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserPasswordFacade;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.PasswordResetTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.IUserPasswordService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.util.MailUtil;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.password.ForgottenPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.KnownPasswordUpdateRequest;
import com.bloxico.ase.userservice.web.model.password.SetPasswordRequest;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.MailUtil.Template.RESET_PASSWORD;

@Slf4j
@Service
@Transactional
public class UserPasswordFacadeImpl implements IUserPasswordFacade {

    private final IUserProfileService userProfileService;
    private final IUserPasswordService userPasswordService;
    private final ITokenService passwordResetTokenService;
    private final MailUtil mailUtil;

    @Autowired
    public UserPasswordFacadeImpl(IUserProfileService userProfileService,
                                  IUserPasswordService userPasswordService,
                                  PasswordResetTokenServiceImpl passwordResetTokenService,
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
        var token = passwordResetTokenService.getOrCreateTokenForUser(userId);
        mailUtil.sendTokenEmail(RESET_PASSWORD, email, token.getValue());
        log.info("UserPasswordFacadeImpl.handleForgotPasswordRequest - end | request: {}", request);
    }

    @Override
    public void resendPasswordToken(ResendTokenRequest request) {
        log.info("UserPasswordFacadeImpl.resendPasswordToken - start | request: {}", request);
        var email = request.getEmail();
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        var token = passwordResetTokenService.getTokenByUserId(userId);
        mailUtil.sendTokenEmail(RESET_PASSWORD, email, token.getValue());
        log.info("UserPasswordFacadeImpl.resendPasswordToken - end | request: {}", request);
    }

    @Override
    public void updateForgottenPassword(ForgottenPasswordUpdateRequest request) {
        log.info("UserPasswordFacadeImpl.updateForgottenPassword - start | request: {}", request);
        var email = request.getEmail();
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        passwordResetTokenService.consumeTokenForUser(request.getTokenValue(), userId);
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
