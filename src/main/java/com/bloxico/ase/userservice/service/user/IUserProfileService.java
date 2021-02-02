package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;

import java.util.Collection;

public interface IUserProfileService {

    UserProfileDto findUserProfileByUserId(long id);

    UserProfileDto updateUserProfile(long id, UpdateUserProfileRequest request);

    UserProfileDto saveUserProfile(UserProfileDto userProfileDto, long principalId);

    EvaluatorDto saveEvaluator(EvaluatorDto evaluatorDto, long principalId);

    ArtOwnerDto saveArtOwner(ArtOwnerDto artOwnerDto, long principalId);

    void fetchTotalOfEvaluatorsForCountries(Collection<CountryDto> countries);

}
