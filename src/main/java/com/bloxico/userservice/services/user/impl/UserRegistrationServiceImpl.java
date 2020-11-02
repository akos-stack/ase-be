package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.RegistrationRequestDto;
import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.entities.user.*;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.repository.user.RegionRepository;
import com.bloxico.userservice.repository.user.RoleRepository;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.util.mappers.EntityDataMapper;
import com.bloxico.userservice.util.mappers.RegistrationRequestMapper;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("userRegistration")
public class UserRegistrationServiceImpl implements IUserRegistrationService {

    private CoinUserRepository coinUserRepository;
    private RoleRepository roleRepository;
    private RegionRepository regionRepository;
    private PasswordEncoder passwordEncoder;
    private EntityDataMapper entityDataMapper = EntityDataMapper.INSTANCE;

    private final String DEFAULT_REGION_NAME = "Netherlands";

    @Autowired
    public UserRegistrationServiceImpl(CoinUserRepository coinUserRepository,
                                       RegionRepository regionRepository,
                                       @Qualifier("roleRepositoryOld") RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder) {

        this.coinUserRepository = coinUserRepository;
        this.roleRepository = roleRepository;
        this.regionRepository = regionRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @Override
    public CoinUserDto registerDisabledUser(RegistrationRequestDto registrationRequestDto) {
        log.debug("Registering user with disabled flag - start , request : {}", registrationRequestDto.toString());

        RegistrationRequestMapper registrationRequestMapper = RegistrationRequestMapper.INSTANCE;

        CoinUser coinUser = registrationRequestMapper.dtoToCoinUser(registrationRequestDto);
        UserProfile userProfile = registrationRequestMapper.dtoToUserProfile(registrationRequestDto);

        if (!userPasswordIsMatching(registrationRequestDto)) {
            log.warn("Matching password is not the same as the password - throwing exception.");
            throw new CoinUserException(ErrorCodes.MATCH_REGISTRATION_PASSWORD_ERROR.getCode());
        }

        if (emailAlreadyExists(coinUser.getEmail())) {
            log.warn("Email already exists in the database - throwing exception.");
            throw new CoinUserException(ErrorCodes.USER_EXISTS.getCode());
        }

        coinUser.setPassword(passwordEncoder.encode(registrationRequestDto.getPassword()));

        Optional<CoinRole> role = roleRepository.findRoleByRoleName(CoinRole.RoleName.USER);

        CoinUserRole coinUserRole = new CoinUserRole();
        coinUserRole.setCoinRole(role.orElseThrow(() -> new EntityNotFoundException(ErrorCodes.ROLE_NOT_FOUND.getCode())));
        coinUserRole.setCoinUser(coinUser);

        coinUser.setCoinUserRoles(Collections.singletonList(coinUserRole));

        String regionName = registrationRequestDto.getRegionName();
        Optional<Region> region = regionRepository.findByRegionName(regionName);
        userProfile.setRegion(region.orElseThrow(() -> new EntityNotFoundException(ErrorCodes.REGION_NOT_FOUND.getCode())));
        userProfile.setCoinUser(coinUser);

        coinUser.setUserProfile(userProfile);

//        coinUser.setEnabled(true);

        coinUserRepository.save(coinUser);

        CoinUserDto coinUserDto = entityDataMapper.coinUserToDto(coinUser);

        log.debug("Registering coinUser with disabled flag - end , returning coinUser: {}", coinUserDto);
        return coinUserDto;
    }

    private boolean userPasswordIsMatching(RegistrationRequestDto requestDto) {
        return requestDto.getPassword().equals(requestDto.getMatchPassword());
    }

    private boolean emailAlreadyExists(String email) {
        Optional<CoinUser> coinUser = coinUserRepository.findByEmailIgnoreCase(email);

        return coinUser.isPresent();
    }

    @Override
    public void enableUser(long userId) {
        log.debug("Enable coinUser - start , userId: {}", userId);

        Optional<CoinUser> op = coinUserRepository.findById(userId);
        CoinUser coinUser = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        coinUser.setEnabled(true);
        coinUserRepository.save(coinUser);

        log.debug("Enable coinUser - end , userId: {}", userId);
    }

    @Transactional
    @Override
    public List<Long> deleteDisabledUsersWithIds(List<Long> ids) {

        List<Long> deletedUsers = new ArrayList<>();

        for (Long id : ids) {
            CoinUser coinUser = coinUserRepository.findById(id).get();
            if (!coinUser.isEnabled()) {
                coinUserRepository.delete(coinUser);

                deletedUsers.add(coinUser.getId());
            }
        }

        return deletedUsers;
    }

    @Override
    public void integrateUserIfNotPresent(String email) {
        if(!emailAlreadyExists(email)) {
           insertNewIntegratedUser(email);
        }
    }

    public void insertNewIntegratedUser(String email) {
        CoinUser coinUser = new CoinUser();
        coinUser.setEmail(email);
        coinUser.setEnabled(true);

        Optional<Region> region = regionRepository.findByRegionName(DEFAULT_REGION_NAME);

        UserProfile userProfile = new UserProfile();
        userProfile.setRegion(region.orElseThrow(() -> new EntityNotFoundException(ErrorCodes.REGION_NOT_FOUND.getCode())));
        userProfile.setCoinUser(coinUser);

        coinUser.setUserProfile(userProfile);

        Optional<CoinRole> role = roleRepository.findRoleByRoleName(CoinRole.RoleName.USER);

        CoinUserRole coinUserRole = new CoinUserRole();
        coinUserRole.setCoinRole(role.orElseThrow(() -> new EntityNotFoundException(ErrorCodes.ROLE_NOT_FOUND.getCode())));
        coinUserRole.setCoinUser(coinUser);

        coinUser.setCoinUserRoles(Collections.singletonList(coinUserRole));

        coinUserRepository.save(coinUser);
    }
}
