package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.IUserRegistrationService;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Service
public class UserRegistrationServiceImpl implements IUserRegistrationService {

    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationServiceImpl(UserProfileRepository userProfileRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder)
    {
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserProfileDto registerDisabledUser(RegistrationRequest request) {
        log.info("UserRegistrationServiceImpl.registerDisabledUser - start | request: {}", request);
        requireNonNull(request);
        if (!request.isPasswordMatching())
            throw ErrorCodes.User.MATCH_REGISTRATION_PASSWORD_ERROR.newException();
        if (userAlreadyExists(request.getEmail()))
            throw ErrorCodes.User.USER_EXISTS.newException();
        var userProfile = MAPPER.toUserProfile(request);
        userProfile.setName(request.extractNameFromEmail());
        userProfile.setPassword(passwordEncoder.encode(request.getPassword()));
        userProfile.addRole(roleRepository.getUserRole());
        userProfile = userProfileRepository.saveAndFlush(userProfile);
        var userProfileDto = MAPPER.toUserProfileDto(userProfile);
        log.info("UserRegistrationServiceImpl.registerDisabledUser - end | request: {}", request);
        return userProfileDto;
    }

    @Override
    public void enableUser(long id) {
        log.info("UserRegistrationServiceImpl.enableUser - start | id: {}", id);
        var user = userProfileRepository
                .findById(id)
                .orElseThrow(ErrorCodes.User.USER_NOT_FOUND::newException);
        user.setEnabled(true);
        user.setUpdaterId(id);
        userProfileRepository.saveAndFlush(user);
        log.info("UserRegistrationServiceImpl.enableUser - end | id: {}", id);
    }

    @Override
    public List<Long> deleteDisabledUsersWithIds(Collection<Long> ids) {
        log.info("UserRegistrationServiceImpl.deleteDisabledUsersWithIds - start | ids: {}", ids);
        requireNonNull(ids);
        if (ids.isEmpty()) return List.of();
        var disabled = userProfileRepository
                .findAllById(ids)
                .stream()
                .filter(not(UserProfile::getEnabled))
                .collect(toList());
        userProfileRepository.deleteInBatch(disabled);
        var deleted = disabled
                .stream()
                .map(UserProfile::getId)
                .collect(toUnmodifiableList());
        log.info("UserRegistrationServiceImpl.deleteDisabledUsersWithIds - end | ids: {}", ids);
        return deleted;
    }

    private boolean userAlreadyExists(String email) {
        return userProfileRepository.findByEmailIgnoreCase(email).isPresent();
    }

}
