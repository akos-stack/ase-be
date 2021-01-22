package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.aspiration.Aspiration;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.aspiration.AspirationRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserRegistrationService;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class UserRegistrationServiceImpl implements IUserRegistrationService {

    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AspirationRepository aspirationRepository;

    @Autowired
    public UserRegistrationServiceImpl(UserProfileRepository userProfileRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder,
                                       AspirationRepository aspirationRepository)
    {
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.aspirationRepository = aspirationRepository;
    }

    @Override
    public UserProfileDto registerDisabledUser(RegistrationRequest request) {
        log.debug("UserRegistrationServiceImpl.registerDisabledUser - start | request: {}", request);
        requireNonNull(request);
        if (!request.isPasswordMatching())
            throw MATCH_REGISTRATION_PASSWORD_ERROR.newException();
        if (userAlreadyExists(request.getEmail()))
            throw USER_EXISTS.newException();
        var userProfile = MAPPER.toUserProfile(request);
        userProfile.setName(request.extractNameFromEmail());
        userProfile.setPassword(passwordEncoder.encode(request.getPassword()));
        userProfile.addRole(roleRepository.getUserRole());
        userProfile.addAllAspirations(getAspirationsByNames(request.getAspirationNames()));
        userProfile = userProfileRepository.saveAndFlush(userProfile);
        var userProfileDto = MAPPER.toDto(userProfile);
        log.debug("UserRegistrationServiceImpl.registerDisabledUser - end | request: {}", request);
        return userProfileDto;
    }

    @Override
    public void enableUser(long id) {
        log.debug("UserRegistrationServiceImpl.enableUser - start | id: {}", id);
        var user = userProfileRepository
                .findById(id)
                .orElseThrow(USER_NOT_FOUND::newException);
        user.setEnabled(true);
        user.setUpdaterId(id);
        userProfileRepository.saveAndFlush(user);
        log.debug("UserRegistrationServiceImpl.enableUser - end | id: {}", id);
    }

    @Override
    public List<Long> deleteDisabledUsersWithIds(Collection<Long> ids) {
        log.debug("UserRegistrationServiceImpl.deleteDisabledUsersWithIds - start | ids: {}", ids);
        requireNonNull(ids);
        if (ids.isEmpty()) return List.of();
        var disabled = userProfileRepository.findAllDisabledByIds(ids);
        userProfileRepository.deleteInBatch(disabled);
        var deleted = disabled
                .stream()
                .map(UserProfile::getId)
                .collect(toUnmodifiableList());
        log.debug("UserRegistrationServiceImpl.deleteDisabledUsersWithIds - end | ids: {}", ids);
        return deleted;
    }

    private boolean userAlreadyExists(String email) {
        return userProfileRepository.findByEmailIgnoreCase(email).isPresent();
    }

    private boolean containsInvalidAspirationName(Collection<String> aspirationNames) {
        var validAspirationNames =
                aspirationRepository
                        .findAll()
                        .stream()
                        .map(a -> a.getRole().getName())
                        .collect(toList());

        return !validAspirationNames.containsAll(aspirationNames);
    }

    public List<Aspiration> getAspirationsByNames(Collection<String> names) {
        var aspirations= aspirationRepository.findAllByRoleNameIn(names);
        var numberOfRequestedAspirations = names.size();

        // CAUTION!
        // Even if request contains invalid aspiration names nothing would happen
        // Invalid aspiration names would simply be filtered out by findAllByRoleNameIn method
        // Exception is thrown just to inform the client that request contains bad data
        if (aspirations.size() != numberOfRequestedAspirations) {
            throw ASPIRATION_NOT_VALID.newException();
        }

        return aspirations;
    }

}
