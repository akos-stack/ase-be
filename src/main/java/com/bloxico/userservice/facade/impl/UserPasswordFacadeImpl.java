package com.bloxico.userservice.facade.impl;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.dto.UpdatePasswordDto;
import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.TokenDto;
import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.facade.IUserPasswordFacade;
import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.services.user.IUserPasswordService;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.util.MailUtil;
import com.bloxico.userservice.util.mappers.ForgotPasswordChangeRequestMapper;
import com.bloxico.userservice.util.mappers.UpdatePasswordRequestMapper;
import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import com.bloxico.userservice.web.model.password.ForgotPasswordInitRequest;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserPasswordFacadeImpl implements IUserPasswordFacade {

    private IUserService userService;
    private IUserPasswordService userPasswordService;
    private ITokenService<PasswordResetToken> passwordResetTokenService;
    private MailUtil mailUtil;

    @Autowired
    public UserPasswordFacadeImpl(IUserService userService,
                                  PasswordTokenServiceImpl passwordResetTokenService,
                                  IUserPasswordService userPasswordService,
                                  MailUtil mailUtil) {
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.userPasswordService = userPasswordService;
        this.mailUtil = mailUtil;
    }

    @Override
    public void handleForgotPasswordRequest(ForgotPasswordInitRequest forgotPasswordInitRequest) {
        log.info("Handle forgot password request - start for request: {}", forgotPasswordInitRequest);
        String email = forgotPasswordInitRequest.getEmail();

        CoinUserDto coinUserDto = userService.findUserByEmail(email);
        TokenDto token = passwordResetTokenService.createNewOrReturnNonExpiredTokenForUser(coinUserDto.getId());

        mailUtil.sendResetPasswordTokenEmail(email, token.getTokenValue());
        log.info("Handle forgot password request - end , email sent to {}", email);
    }


    @Override
    @Transactional
    public void updateForgottenPassword(ForgotPasswordChangeRequest forgotPasswordChangeRequest) {
        log.info("Updating forgotten password - start , request {}", forgotPasswordChangeRequest);

        ForgotPasswordDto forgotPasswordDto = ForgotPasswordChangeRequestMapper.INSTANCE.requestToDto(forgotPasswordChangeRequest);

        CoinUserDto coinUserDto = userService.findUserByEmail(forgotPasswordChangeRequest.getEmail());

        passwordResetTokenService.consumeTokenForUser(coinUserDto.getId(), forgotPasswordDto.getTokenValue());

        userPasswordService.updateForgottenPassword(coinUserDto.getId(), forgotPasswordDto);

        log.info("Updating forgotten password - end , request {}", forgotPasswordChangeRequest);
    }

    @Override
    public void updateKnownPassword(String email, UpdatePasswordRequest updatePasswordRequest) {
        log.info("Updating known password start -  email {}", email);

        UpdatePasswordDto updatePasswordDto = UpdatePasswordRequestMapper.INSTANCE.requestToDto(updatePasswordRequest);

        userPasswordService.updateKnownPassword(email, updatePasswordDto);

        log.info("Updating known password end -  email {}", email);

    }

    @Override
    public void resendPasswordToken(String email) {
        log.info("Resend password token start - email: {}", email);

        CoinUserDto coinUserDto = userService.findUserByEmail(email);

        TokenDto tokenDto = passwordResetTokenService.getTokenByUserId(coinUserDto.getId());

        mailUtil.sendResetPasswordTokenEmail(email, tokenDto.getTokenValue());

        log.info("Resend password token end - email: {}", email);
    }

    @Override
    public void setNewPasword(String email, String password) {
        log.info("Setting new password - start , email: {}", email);

        userPasswordService.setNewPassword(email, password);

        log.info("Setting new password - end , email: {}", email);
    }
}
