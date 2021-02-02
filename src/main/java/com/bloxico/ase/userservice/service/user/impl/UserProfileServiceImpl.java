package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.userservice.dto.CountryIdTotalOfEvaluatorsPair;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.repository.user.profile.*;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.USER_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final ArtOwnerRepository artOwnerRepository;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  EvaluatorRepository evaluatorRepository,
                                  ArtOwnerRepository artOwnerRepository)
    {
        this.userProfileRepository = userProfileRepository;
        this.evaluatorRepository = evaluatorRepository;
        this.artOwnerRepository = artOwnerRepository;
    }

    @Override
    public UserProfileDto findUserProfileByUserId(long id) {
        log.debug("UserProfileServiceImpl.findUserProfileByUserId - start | id: {}", id);
        var userProfileDto = userProfileRepository
                .findByUserId(id)
                .map(MAPPER::toDto)
                .orElseThrow(USER_NOT_FOUND::newException);
        log.debug("UserProfileServiceImpl.findUserProfileByUserId - end | id: {}", id);
        return userProfileDto;
    }

    @Override
    public UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request) {
        log.debug("UserProfileServiceImpl.updateUserProfile - start | id: {}, request: {}", id, request);
        requireNonNull(request);
        var userProfile = userProfileRepository
                .findByUserId(id)
                .orElseThrow(USER_NOT_FOUND::newException);
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhone(request.getPhone());
        userProfile.setUpdaterId(id);
        userProfile = userProfileRepository.saveAndFlush(userProfile);
        var userProfileDto = MAPPER.toDto(userProfile);
        log.debug("UserProfileServiceImpl.updateUserProfile - end | id: {}, request: {}", id, request);
        return userProfileDto;
    }

    @Override
    public UserProfileDto saveUserProfile(UserProfileDto userProfileDto, long principalId) {
        log.debug("UserProfileServiceImpl.saveUserProfile - start | userProfileDto: {}, principalId: {}", userProfileDto, principalId);
        requireNonNull(userProfileDto);
        var userProfile = MAPPER.toEntity(userProfileDto);
        userProfile.setCreatorId(principalId);
        var dto = MAPPER.toDto(userProfileRepository.saveAndFlush(userProfile));
        log.debug("UserProfileServiceImpl.saveUserProfile - end | userProfileDto: {}, principalId: {}", userProfileDto, principalId);
        return dto;
    }

    @Override
    public EvaluatorDto saveEvaluator(EvaluatorDto evaluatorDto, long principalId) {
        log.debug("UserProfileServiceImpl.saveEvaluator - start | evaluatorDto: {}, principalId: {}", evaluatorDto, principalId);
        requireNonNull(evaluatorDto);
        var evaluator = MAPPER.toEntity(evaluatorDto);
        evaluator.setCreatorId(principalId);
        var dto = MAPPER.toDto(evaluatorRepository.saveAndFlush(evaluator));
        log.debug("UserProfileServiceImpl.saveEvaluator - end | evaluatorDto: {}, principalId: {}", evaluatorDto, principalId);
        return dto;
    }

    public ArtOwnerDto saveArtOwner(ArtOwnerDto artOwnerDto, long principalId) {
        log.debug("UserProfileServiceImpl.saveArtOwner - start | artOwnerDto: {}, principalId: {}", artOwnerDto, principalId);
        requireNonNull(artOwnerDto);
        var artOwner = MAPPER.toEntity(artOwnerDto);
        artOwner.setCreatorId(principalId);
        var dto = MAPPER.toDto(artOwnerRepository.saveAndFlush(artOwner));
        log.debug("UserProfileServiceImpl.saveArtOwner - start | artOwnerDto: {}, principalId: {}", artOwnerDto, principalId);
        return dto;
    }

    @Override
    public void fetchTotalOfEvaluatorsForCountries(Collection<CountryDto> countries) {
        log.debug("UserProfileServiceImpl.countTotalOfEvaluatorsForCountries - start | countries: {}", countries);
        var countriesMap = countries.stream()
                .collect(toMap(CountryDto::getId, country -> country));
        var pairs = evaluatorRepository
                .countTotalOfEvaluatorsForEachCountryIdIn(countriesMap.keySet());
        pairs
                .forEach(p -> {
                    var countryId = p.getCountryId();
                    var totalOfEvaluators = p.getTotalOfEvaluators();
                    countriesMap.get(countryId)
                            .getCountryEvaluationDetails()
                            .setTotalOfEvaluators(totalOfEvaluators);
                });
        log.debug("UserProfileServiceImpl.countTotalOfEvaluatorsForCountries - end | countries: {}", countries);
    }

}
