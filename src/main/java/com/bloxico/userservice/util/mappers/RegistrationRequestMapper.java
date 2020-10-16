package com.bloxico.userservice.util.mappers;

import com.bloxico.userservice.dto.RegistrationRequestDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.Region;
import com.bloxico.userservice.entities.user.UserProfile;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
/**
 * Top to bottom naming logic is used - since Registration request is the top POJO that will be further mapped into other POJOS from the lower levels
 */
public interface RegistrationRequestMapper {

    RegistrationRequestMapper INSTANCE = Mappers.getMapper(RegistrationRequestMapper.class);

    RegistrationRequestDto requestToDto(RegistrationRequest request);

    CoinUser dtoToCoinUser(RegistrationRequestDto requestDto);

    @Mapping(target = "region", ignore = true)
    UserProfile dtoToUserProfile(RegistrationRequestDto requestDto);

    Region dtoToRegion(RegistrationRequestDto requestDto);

}