package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserPasswordService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class UserPasswordServiceImpl implements IUserPasswordService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserPasswordServiceImpl(UserProfileRepository userProfileRepository,
                                   PasswordEncoder passwordEncoder)
    {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateForgottenPassword(long userProfileId, String newPassword) {
        log.debug("UserPasswordServiceImpl.updateForgottenPassword - start | userProfileId: {}", userProfileId);
        requireNonNull(newPassword);
        var userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(USER_NOT_FOUND::newException);
        doSetPassword(userProfile, newPassword);
        log.debug("UserPasswordServiceImpl.updateForgottenPassword - end | userProfileId: {}", userProfileId);
    }

    @Override
    public void updateKnownPassword(long principalId, String oldPassword, String newPassword) {
        log.debug("UserPasswordServiceImpl.updateKnownPassword - start | principalId: {}", principalId);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var userProfile = userProfileRepository
                .findById(principalId)
                .orElseThrow(USER_NOT_FOUND::newException);
        if (!passwordEncoder.matches(oldPassword, userProfile.getPassword()))
            throw ErrorCodes.User.OLD_PASSWORD_DOES_NOT_MATCH.newException();
        doSetPassword(userProfile, newPassword);
        log.debug("UserPasswordServiceImpl.updateKnownPassword - end | principalId: {}", principalId);
    }

    @Override
    public void setNewPassword(long principalId, String password) {
        log.debug("UserPasswordServiceImpl.setNewPassword - start | principalId: {}", principalId);
        requireNonNull(password);
        var userProfile = userProfileRepository
                .findById(principalId)
                .orElseThrow(USER_NOT_FOUND::newException);
        doSetPassword(userProfile, password);
        log.debug("UserPasswordServiceImpl.setNewPassword - end | principalId: {}", principalId);
    }

    private void doSetPassword(UserProfile userProfile, String password) {
        var encoded = passwordEncoder.encode(password);
        userProfile.setPassword(encoded);
        userProfile.setUpdaterId(userProfile.getId());
        userProfileRepository.saveAndFlush(userProfile);
    }

}
