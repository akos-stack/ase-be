package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.user.EvaluatorRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final LocationRepository locationRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  EvaluatorRepository evaluatorRepository,
                                  LocationRepository locationRepository,
                                  RoleRepository roleRepository)
    {
        this.userProfileRepository = userProfileRepository;
        this.evaluatorRepository = evaluatorRepository;
        this.locationRepository = locationRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserProfileDto findUserProfileById(long id) {
        log.debug("UserProfileServiceImpl.findUserProfileById - start | id: {}", id);
        var userProfile = userProfileRepository
                .findById(id)
                .orElseThrow(USER_NOT_FOUND::newException);
        var userProfileDto = MAPPER.toDto(userProfile);
        userProfileDto.setAspirationNames(getAspirationNamesFromUserProfile(userProfile));
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
    public UserProfileDto saveEnabledUserProfile(UserProfileDto dto) {
        log.debug("UserProfileServiceImpl.saveUserProfile - start | dto: {}", dto);
        requireNonNull(dto);
        var userProfile = MAPPER.toEntity(dto);
        userProfile.setEnabled(true);
        var userProfileDto = MAPPER.toDto(userProfileRepository.saveAndFlush(userProfile));
        log.debug("UserProfileServiceImpl.saveUserProfile - end | dto: {}", dto);
        return userProfileDto;
    }

    @Override
    public UserProfileDto updateLocation(UserProfileDto userProfileDto, LocationDto locationDto) {
        log.debug("UserProfileServiceImpl.updateLocation - start | userProfileDto: {}, locationDto: {}", userProfileDto, locationDto);
        requireNonNull(userProfileDto);
        requireNonNull(locationDto);
        var userProfile = userProfileRepository
                .findById(userProfileDto.getId())
                .orElseThrow(USER_NOT_FOUND::newException);
        userProfile.setLocation(locationRepository.fetchById(locationDto.getId()));
        userProfile.setUpdaterId(userProfile.getId());
        var updatedUserProfileDto = MAPPER.toDto(userProfileRepository.saveAndFlush(userProfile));
        log.debug("UserProfileServiceImpl.updateLocation - end | userProfileDto: {}, locationDto: {}", userProfileDto, locationDto);
        return updatedUserProfileDto;
    }

    @Override
    public EvaluatorDto saveEvaluator(EvaluatorDto dto, long principalId) {
        log.debug("UserProfileServiceImpl.saveEvaluator - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        requireHasRole(dto.getUserProfile(), EVALUATOR);
        var evaluator = MAPPER.toEntity(dto);
        evaluator.setCreatorId(principalId);
        var evaluatorDto = MAPPER.toDto(evaluatorRepository.saveAndFlush(evaluator));
        log.debug("UserProfileServiceImpl.saveEvaluator - end | dto: {}, principalId: {}", dto, principalId);
        return evaluatorDto;
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
    public List<UserProfileDto> findUsersByEmailOrRole(String email, String role, int page, int size, String sort) {
        log.debug("UserProfileServiceImpl.findUsersByEmailOrRole - start | email: {}, role {}, page: {}, size: {}", email, role, page, size);
        role = validateRole(role);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var userProfiles = userProfileRepository.findDistinctByEmailContainingAndRoles_NameContaining(email, role, pageable);
        var userProfileDtos = userProfiles
                .stream()
                .map(MAPPER::toDto)
                .collect(Collectors.toList());
        log.debug("UserProfileServiceImpl.findUsersByEmailOrRole - end | email: {}, role {}, page: {}, size: {}", email, role, page, size);
        return userProfileDtos;
    }

    private static void requireHasRole(UserProfileDto userProfileDto, String role) {
        userProfileDto
                .getRoles()
                .stream()
                .map(RoleDto::getName)
                .filter(role::equals)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("User: " + userProfileDto + " must have role: " + role));
    }

    private String validateRole(String role) {
        if(!StringUtils.isEmpty(role)) {
            roleRepository.findByNameIgnoreCase(role).orElseThrow(ROLE_NOT_FOUND::newException);
        }
        return role != null ? role : "";
    }

    private Set<String> getAspirationNamesFromUserProfile(UserProfile u) {
        return u
                .getAspirations()
                .stream()
                .map(a -> a.getRole().getName())
                .collect(Collectors.toSet());
    }

}
