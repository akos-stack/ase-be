package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto findUserById(long id) {
        log.debug("UserServiceImpl.findUserById - start | id: {}", id);
        var userDto = userRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(USER_NOT_FOUND::newException);
        log.debug("UserServiceImpl.findUserById - end | id: {}", id);
        return userDto;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        log.debug("UserServiceImpl.findUserByEmail - start | email: {}", email);
        requireNonNull(email);
        var userDto = userRepository
                .findByEmailIgnoreCase(email)
                .map(MAPPER::toDto)
                .orElseThrow(USER_NOT_FOUND::newException);
        log.debug("UserServiceImpl.findUserByEmail - end | email: {}", email);
        return userDto;
    }

    @Override
    public List<UserDto> findUsersByEmailOrRole(String email, String role, int page, int size, String sort) {
        log.debug("UserProfileServiceImpl.findUsersByEmailOrRole - start | email: {}, role {}, page: {}, size: {}", email, role, page, size);
        role = validateRole(role);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var userProfiles = userRepository.findDistinctByEmailContainingAndRoles_NameContaining(email, role, pageable);
        var userProfileDtos = userProfiles
                .stream()
                .map(MAPPER::toDto)
                .collect(Collectors.toList());
        log.debug("UserProfileServiceImpl.findUsersByEmailOrRole - end | email: {}, role {}, page: {}, size: {}", email, role, page, size);
        return userProfileDtos;
    }

    private String validateRole(String role) {
        if (role != null && !role.isBlank())
            roleRepository
                    .findByNameIgnoreCase(role)
                    .orElseThrow(ROLE_NOT_FOUND::newException);
        return role != null ? role : "";
    }

    @Override
    public UserDto saveDisabledUser(UserDto userDto) {
        log.debug("UserServiceImpl.saveDisabledUser - start | userDto: {}", userDto);
        requireNonNull(userDto);
        if (userAlreadyExists(userDto.getEmail()))
            throw USER_EXISTS.newException();
        var user = MAPPER.toEntity(userDto);
        user.encodePassword(passwordEncoder);
        user.addRole(roleRepository.getUserRole());
        user = userRepository.saveAndFlush(user);
        var dto = MAPPER.toDto(user);
        log.debug("UserServiceImpl.saveDisabledUser - end | userDto: {}", userDto);
        return dto;
    }

    @Override
    public UserDto saveEnabledUser(UserDto userDto) {
        log.debug("UserServiceImpl.saveEnabledUser - start | userDto: {}", userDto);
        requireNonNull(userDto);
        if (userAlreadyExists(userDto.getEmail()))
            throw USER_EXISTS.newException();
        var user = MAPPER.toEntity(userDto);
        user.setEnabled(true);
        var dto = MAPPER.toDto(userRepository.saveAndFlush(user));
        log.debug("UserServiceImpl.saveEnabledUser - end | userDto: {}", userDto);
        return dto;
    }

    @Override
    public void enableUser(long id) {
        log.debug("UserServiceImpl.enableUser - start | id: {}", id);
        var user = userRepository
                .findById(id)
                .orElseThrow(USER_NOT_FOUND::newException);
        user.setEnabled(true);
        user.setUpdaterId(id);
        userRepository.saveAndFlush(user);
        log.debug("UserServiceImpl.enableUser - end | id: {}", id);
    }

    @Override
    public void disableUser(long userId, long principalId) {
        log.debug("UserServiceImpl.disableUser - start | userId: {}, principalId: {}", userId, principalId);
        var user = userRepository
                .findById(userId)
                .orElseThrow(USER_NOT_FOUND::newException);
        user.setEnabled(false);
        user.setUpdaterId(principalId);
        userRepository.saveAndFlush(user);
        log.debug("UserServiceImpl.disableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public List<Long> deleteDisabledUsersWithIds(Collection<Long> ids) {
        log.debug("UserServiceImpl.deleteDisabledUsersWithIds - start | ids: {}", ids);
        requireNonNull(ids);
        if (ids.isEmpty()) return List.of();
        var disabled = userRepository.findAllDisabledByIds(ids);
        userRepository.deleteInBatch(disabled);
        var deleted = disabled
                .stream()
                .map(User::getId)
                .collect(toUnmodifiableList());
        log.debug("UserServiceImpl.deleteDisabledUsersWithIds - end | ids: {}", ids);
        return deleted;
    }

    @Override
    public void updatePassword(long principalId, String oldPassword, String newPassword) {
        log.debug("UserServiceImpl.updatePassword - start | principalId: {}", principalId);
        requireNonNull(oldPassword);
        requireNonNull(newPassword);
        var user = userRepository
                .findById(principalId)
                .orElseThrow(USER_NOT_FOUND::newException);
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw ErrorCodes.User.OLD_PASSWORD_DOES_NOT_MATCH.newException();
        doSetPassword(user, newPassword);
        log.debug("UserServiceImpl.updatePassword - end | principalId: {}", principalId);
    }

    @Override
    public void setNewPassword(long principalId, String password) {
        log.debug("UserServiceImpl.setNewPassword - start | principalId: {}", principalId);
        requireNonNull(password);
        var user = userRepository
                .findById(principalId)
                .orElseThrow(USER_NOT_FOUND::newException);
        doSetPassword(user, password);
        log.debug("UserServiceImpl.setNewPassword - end | principalId: {}", principalId);
    }

    private boolean userAlreadyExists(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    private void doSetPassword(User user, String password) {
        var encoded = passwordEncoder.encode(password);
        user.setPassword(encoded);
        user.setUpdaterId(user.getId());
        userRepository.saveAndFlush(user);
    }

}
