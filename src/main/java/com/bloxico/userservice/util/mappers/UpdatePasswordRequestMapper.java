package com.bloxico.userservice.util.mappers;

import com.bloxico.userservice.dto.UpdatePasswordDto;
import com.bloxico.userservice.web.model.password.UpdatePasswordRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdatePasswordRequestMapper {

    UpdatePasswordRequestMapper INSTANCE = Mappers.getMapper(UpdatePasswordRequestMapper.class);

    UpdatePasswordDto requestToDto(UpdatePasswordRequest updatePasswordRequest);
}
