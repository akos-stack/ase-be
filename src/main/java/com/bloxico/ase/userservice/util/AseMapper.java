package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.address.City;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.entity.address.Location;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.Evaluator;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AseMapper {

    AseMapper MAPPER = Mappers.getMapper(AseMapper.class);

    CityDto toDto(City entity);

    CountryDto toDto(Country entity);

    LocationDto toDto(Location entity);

    UserProfileDto toDto(UserProfile entity);

    EvaluatorDto toDto(Evaluator entity);

    RoleDto toDto(Role entity);

    TokenDto toDto(Token entity);

    OAuthAccessTokenDto toDto(OAuthAccessToken entity);

    City toEntity(CityDto dto);

    Country toEntity(CountryDto dto);

    Location toEntity(LocationDto dto);

    UserProfile toEntity(UserProfileDto dto);

    Evaluator toEntity(EvaluatorDto dto);

    PendingEvaluatorDto toDto(PendingEvaluator entity);

    UserProfile toUserProfile(RegistrationRequest request);

    @Mapping(target = "value", source = "tokenId")
    @Mapping(target = "expiryDate", source = "expiration")
    BlacklistedToken toBlacklistedToken(OAuthAccessTokenDto token);

    @Mapping(target = "name", source = "country")
    CountryDto toCountryDto(SubmitEvaluatorRequest request);

    @Mapping(ignore = true, target = "country")
    @Mapping(target = "name", source = "city")
    CityDto toCityDto(SubmitEvaluatorRequest request);

    @Mapping(ignore = true, target = "city")
    LocationDto toLocationDto(SubmitEvaluatorRequest request);

    @Mapping(target = "name", source = "username")
    UserProfileDto toUserProfileDto(SubmitEvaluatorRequest request);

    EvaluatorDto toEvaluatorDto(SubmitEvaluatorRequest request);

    PendingEvaluator toPendingEvaluator(IPendingEvaluatorRequest request);

}
