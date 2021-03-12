package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import com.bloxico.ase.userservice.service.user.IUserService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.user.SearchUsersRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.*;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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
    public Page<UserDto> findUsersByEmailOrRole(SearchUsersRequest request, PageRequest page) {
        log.debug("UserServiceImpl.findUsersByEmailOrRole - start | request: {}, page: {}", request, page);
        requireNonNull(request);
        requireNonNull(page);
        var userDtos = userRepository
                .findDistinctByEmailContainingAndRoles_NameContaining(
                        request.getEmail(),
                        requireNullOrEmptyOrExists(request.getRole()),
                        page.toPageable())
                .map(MAPPER::toDto);
        log.debug("UserServiceImpl.findUsersByEmailOrRole - end | request: {}, page: {}", request, page);
        return userDtos;
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.debug("UserServiceImpl.saveUser - start | userDto: {}", userDto);
        requireNonNull(userDto);
        if (userAlreadyExists(userDto.getEmail()))
            throw USER_EXISTS.newException();
        var user = MAPPER.toEntity(userDto);
        if (isNotEmpty(userDto.getAspirationNames()))
            user.addAllAspirations(findAllAspirationsByNames(userDto.getAspirationNames()));
        user.encodePassword(passwordEncoder);
        var dto = MAPPER.toDto(userRepository.saveAndFlush(user));
        log.debug("UserServiceImpl.saveUser - start | userDto: {}", userDto);
        return dto;
    }

    @Override
    public void enableUser(long userId, long principalId) {
        log.debug("UserServiceImpl.enableUser - start | userId: {}, principalId: {}", userId, principalId);
        var user = userRepository
                .findById(userId)
                .orElseThrow(USER_NOT_FOUND::newException);
        user.setEnabled(true);
        user.setUpdaterId(principalId);
        userRepository.saveAndFlush(user);
        log.debug("UserServiceImpl.enableUser - end | userId: {}, principalId: {}", userId, principalId);
    }

    @Override
    public void disableUser(long userId) {
        log.debug("UserServiceImpl.disableUser - start | userId: {}", userId);
        var user = userRepository
                .findById(userId)
                .orElseThrow(USER_NOT_FOUND::newException);
        user.setEnabled(false);
        userRepository.saveAndFlush(user);
        log.debug("UserServiceImpl.disableUser - end | userId: {}", userId);
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

    private List<Role> findAllAspirationsByNames(Collection<String> names) {
        var aspiredRoles = roleRepository.findAllByNameIgnoreCaseIn(names);
        if (aspiredRoles.size() != names.size())
            throw ROLE_NOT_FOUND.newException();
        return aspiredRoles;
    }

    private String requireNullOrEmptyOrExists(String role) {
        return Optional
                .ofNullable(role)
                .map(String::trim)
                .filter(not(String::isEmpty))
                .map(doto(roleRepository::getRole))
                .orElse("");
    }

}
