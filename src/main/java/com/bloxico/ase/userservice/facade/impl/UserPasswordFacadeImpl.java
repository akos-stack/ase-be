package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IUserPasswordFacade;
import com.bloxico.ase.userservice.service.token.ITokenService;
import com.bloxico.ase.userservice.service.token.impl.PasswordResetTokenServiceImpl;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.util.MailUtil;
import com.bloxico.ase.userservice.web.model.password.*;
import com.bloxico.ase.userservice.web.model.token.ResendTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.config.security.AseSecurityContext.getPrincipalId;
import static com.bloxico.ase.userservice.util.MailUtil.Template.RESET_PASSWORD;

@Slf4j
@Service
@Transactional
public class UserPasswordFacadeImpl implements IUserPasswordFacade {

    private final IUserService userService;
    private final ITokenService passwordResetTokenService;
    private final MailUtil mailUtil;

    @Autowired
    public UserPasswordFacadeImpl(IUserService userService,
                                  PasswordResetTokenServiceImpl passwordResetTokenService,
                                  MailUtil mailUtil)
    {
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailUtil = mailUtil;
    }

    @Override
    public void handleForgotPasswordRequest(ForgotPasswordRequest request) {
        log.info("UserPasswordFacadeImpl.handleForgotPasswordRequest - start | request: {}", request);
        var email = request.getEmail();
        var userId = userService.findUserByEmail(email).getId();
        var token = passwordResetTokenService.getOrCreateTokenForUser(userId);
        mailUtil.sendTokenEmail(RESET_PASSWORD, email, token.getValue());
        log.info("UserPasswordFacadeImpl.handleForgotPasswordRequest - end | request: {}", request);
    }

    @Override
    public void resendPasswordToken(ResendTokenRequest request) {
        log.info("UserPasswordFacadeImpl.resendPasswordToken - start | request: {}", request);
        var email = request.getEmail();
        var userId = userService.findUserByEmail(email).getId();
        var token = passwordResetTokenService.getTokenByUserId(userId);
        mailUtil.sendTokenEmail(RESET_PASSWORD, email, token.getValue());
        log.info("UserPasswordFacadeImpl.resendPasswordToken - end | request: {}", request);
    }

    @Override
    public void updateForgottenPassword(ForgottenPasswordUpdateRequest request) {
        log.info("UserPasswordFacadeImpl.updateForgottenPassword - start | request: {}", request);
        var email = request.getEmail();
        var userId = userService.findUserByEmail(email).getId();
        passwordResetTokenService.consumeToken(request.getTokenValue());
        userService.setNewPassword(userId, request.getNewPassword());
        log.info("UserPasswordFacadeImpl.updateForgottenPassword - end | request: {}", request);
    }

    @Override
    public void updateKnownPassword(KnownPasswordUpdateRequest request) {
        log.info("UserPasswordFacadeImpl.updateKnownPassword - start | request: {}", request);
        userService.updatePassword(getPrincipalId(), request.getOldPassword(), request.getNewPassword());
        log.info("UserPasswordFacadeImpl.updateKnownPassword - end | request: {}", request);
    }

    @Override
    public void setNewPassword(SetPasswordRequest request) {
        log.info("UserPasswordFacadeImpl.setNewPassword - start | request: {}", request);
        userService.setNewPassword(getPrincipalId(), request.getPassword());
        log.info("UserPasswordFacadeImpl.setNewPassword - end | request: {}", request);
    }

}
