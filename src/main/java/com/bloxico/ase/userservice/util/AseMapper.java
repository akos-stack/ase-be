package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.*;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.*;
import com.bloxico.ase.userservice.web.model.address.CreateCountryRequest;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.user.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AseMapper {

    AseMapper MAPPER = Mappers.getMapper(AseMapper.class);

    // ENTITY -> DTO

    UserDto toDto(User user);

    RoleDto toDto(Role entity);

    CityDto toDto(City entity);

    CountryDto toDto(Country entity);

    LocationDto toDto(Location entity);

    UserProfileDto toDto(UserProfile entity);

    EvaluatorDto toDto(Evaluator entity);

    ArtOwnerDto toDto(ArtOwner entity);

    TokenDto toDto(Token entity);

    OAuthAccessTokenDto toDto(OAuthAccessToken entity);

    PendingEvaluatorDto toDto(PendingEvaluator entity);

    RegionDto toDto(Region entity);

    CountryEvaluationDetailsDto toDto(CountryEvaluationDetails entity);

    // DTO -> ENTITY

    User toEntity(UserDto dto);

    City toEntity(CityDto dto);

    Country toEntity(CountryDto dto);

    Location toEntity(LocationDto dto);

    UserProfile toEntity(UserProfileDto dto);

    Evaluator toEntity(EvaluatorDto dto);

    ArtOwner toEntity(ArtOwnerDto dto);

    Region toEntity(RegionDto dto);

    CountryEvaluationDetails toEntity(CountryEvaluationDetailsDto dto);

    // OTHER

    @Mapping(target = "name", source = "username")
    UserDto toUserDto(RegistrationRequest request);

    @Mapping(target = "name", source = "username")
    UserDto toUserDto(ISubmitUserProfileRequest request);

    @Mapping(target = "value", source = "tokenId")
    @Mapping(target = "expiryDate", source = "expiration")
    BlacklistedToken toBlacklistedToken(OAuthAccessTokenDto token);

    @Mapping(target = "name", source = "country")
    CountryDto toCountryDto(ISubmitUserProfileRequest request);

    @Mapping(ignore = true, target = "country")
    @Mapping(target = "name", source = "city")
    CityDto toCityDto(ISubmitUserProfileRequest request);

    @Mapping(ignore = true, target = "city")
    LocationDto toLocationDto(ISubmitUserProfileRequest request);

    UserProfileDto toUserProfileDto(ISubmitUserProfileRequest request);

    EvaluatorDto toEvaluatorDto(SubmitEvaluatorRequest request);

    ArtOwnerDto toArtOwnerDto(SubmitArtOwnerRequest request);

    PendingEvaluator toPendingEvaluator(IPendingEvaluatorRequest request);

    RegionDto toRegionDto(CreateRegionRequest request);

    default CountryDto toDto(CreateCountryRequest request) {
        var regionDto = new RegionDto();
        regionDto.setName(request.getRegion());
        var evaluationDetailsDto = new CountryEvaluationDetailsDto();
        evaluationDetailsDto.setPricePerEvaluation(request.getPricePerEvaluation());
        evaluationDetailsDto.setAvailabilityPercentage(request.getAvailabilityPercentage());
        var countryDto = new CountryDto();
        countryDto.setName(request.getName());
        countryDto.setRegion(regionDto);
        countryDto.setCountryEvaluationDetails(evaluationDetailsDto);
        return countryDto;
    }

}
