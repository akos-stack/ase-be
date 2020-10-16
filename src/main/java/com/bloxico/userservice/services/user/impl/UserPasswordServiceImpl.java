package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.dto.UpdatePasswordDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.services.user.IUserPasswordService;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("userPassword")
public class UserPasswordServiceImpl implements IUserPasswordService {

    private CoinUserRepository coinUserRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserPasswordServiceImpl(CoinUserRepository coinUserRepository,
                                   PasswordEncoder passwordEncoder) {
        this.coinUserRepository = coinUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateForgottenPassword(long userId, ForgotPasswordDto forgotPasswordDto) {
        log.debug("Update forgotten password - start , user Id: {}", userId);

        Optional<CoinUser> op = coinUserRepository.findById(userId);
        CoinUser coinUser = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        updateUserPassword(coinUser, forgotPasswordDto.getNewPassword());

        log.debug("Update forgotten password - end , user Id: {}", userId);
    }

    @Override
    public void updateKnownPassword(String email, UpdatePasswordDto updatePasswordDto) {
        log.debug("Updating known password - start , email: {}", email);

        Optional<CoinUser> op = coinUserRepository.findByEmailIgnoreCase(email);
        CoinUser user = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        if (!passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword())) {
            log.warn("Match password is not the same as the original - throwing Exception.");
            throw new CoinUserException(ErrorCodes.OLD_PASSWORD_DOES_NOT_MATCH.getCode());
        }

        updateUserPassword(user, updatePasswordDto.getNewPassword());
        log.debug("Updating known password - end , email: {}", email);
    }

    @Override
    public void setNewPassword(String email, String newPassword) {
        log.debug("Setting new password - start,  email: {}");

        Optional<CoinUser> op = coinUserRepository.findByEmailIgnoreCase(email);
        CoinUser user = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        updateUserPassword(user, newPassword);

        log.debug("Setting new password - end,  email: {}");
    }

    private void updateUserPassword(CoinUser user, String newPassword) {

        user.setPassword(passwordEncoder.encode(newPassword));
        coinUserRepository.save(user);

    }
}
