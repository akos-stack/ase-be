package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.util.mapper.EntityToDtoMapper;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserProfileDto findUserProfileByEmail(String email) {
        log.debug("UserProfileServiceImpl.findUserByEmail - start | email: {}", email);
        requireNonNull(email);
        var userProfile = userProfileRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(ErrorCodes.User.USER_DOES_NOT_EXIST::newException);
        log.debug("UserProfileServiceImpl.findUserByEmail - end | email: {}", email);
        return EntityToDtoMapper.INSTANCE.userProfile(userProfile);
    }

    @Override
    public void checkPassword(String raw, String encoded) {
        log.debug("UserProfileServiceImpl.checkPassword - start | encoded: {}", encoded);
        requireNonNull(raw);
        requireNonNull(encoded);
        if (!passwordEncoder.matches(raw, encoded))
            throw ErrorCodes.User.USER_DOES_NOT_EXIST.newException();
        log.debug("UserProfileServiceImpl.checkPassword - end | encoded: {}", encoded);
    }

}
