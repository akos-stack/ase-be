package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileDto findUserProfileById(long id) {
        log.debug("UserProfileServiceImpl.findUserProfileById - start | id: {}", id);
        var userProfileDto = userProfileRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(USER_NOT_FOUND::newException);
        log.debug("UserProfileServiceImpl.findUserProfileById - end | id: {}", id);
        return userProfileDto;
    }

    @Override
    public UserProfileDto findUserProfileByEmail(String email) {
        log.debug("UserProfileServiceImpl.findUserByEmail - start | email: {}", email);
        requireNonNull(email);
        var userProfileDto = userProfileRepository
                .findByEmailIgnoreCase(email)
                .map(MAPPER::toDto)
                .orElseThrow(USER_NOT_FOUND::newException);
        log.debug("UserProfileServiceImpl.findUserByEmail - end | email: {}", email);
        return userProfileDto;
    }

    @Override
    public UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request) {
        log.debug("UserProfileServiceImpl.updateUserProfile - start | id: {}, request: {}", id, request);
        requireNonNull(request);
        var userProfile = userProfileRepository
                .findById(id)
                .orElseThrow(USER_NOT_FOUND::newException);
        userProfile.setName(request.getName());
        userProfile.setPhone(request.getPhone());
        userProfile.setUpdaterId(id);
        userProfile = userProfileRepository.saveAndFlush(userProfile);
        var userProfileDto = MAPPER.toDto(userProfile);
        log.debug("UserProfileServiceImpl.updateUserProfile - end | id: {}, request: {}", id, request);
        return userProfileDto;
    }

    @Override
    public void disableUser(long userId, long principalId) {
        log.debug("UserProfileServiceImpl.disableUser - start | userId: {}, principalId: {}", userId, principalId);
        var userProfile = userProfileRepository
                .findById(userId)
                .orElseThrow(USER_NOT_FOUND::newException);
        userProfile.setEnabled(false);
        userProfile.setUpdaterId(principalId);
        userProfileRepository.saveAndFlush(userProfile);
        log.debug("UserProfileServiceImpl.disableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public List<UserProfileDto> findUsersByEmail(String email, int page, int size, String sort) {
        log.debug("UserProfileServiceImpl.findUsersByEmail - start | email: {}, page: {}, size: {}", email, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var userProfiles = userProfileRepository.findAllByEmailContaining(email, pageable);
        var userProfileDtos = userProfiles
                .stream()
                .map(user -> MAPPER.toDto(user))
                .collect(Collectors.toList());
        log.debug("UserProfileServiceImpl.findUsersByEmail - end | email: {}, page: {}, size: {}", email, page, size);
        return userProfileDtos;
    }

}
