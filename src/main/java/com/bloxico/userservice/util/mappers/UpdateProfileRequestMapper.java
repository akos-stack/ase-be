package com.bloxico.userservice.util.mappers;

import com.bloxico.userservice.dto.UpdateProfileDto;
import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateProfileRequestMapper {

    UpdateProfileRequestMapper INSTANCE = Mappers.getMapper(UpdateProfileRequestMapper.class);

    UpdateProfileDto requestToDto(UpdateProfileRequest updateProfileRequest);
}
