package com.bloxico.userservice.util.mappers;

import com.bloxico.userservice.dto.ForgotPasswordDto;
import com.bloxico.userservice.web.model.password.ForgotPasswordChangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ForgotPasswordChangeRequestMapper {

    ForgotPasswordChangeRequestMapper INSTANCE = Mappers.getMapper(ForgotPasswordChangeRequestMapper.class);

    ForgotPasswordDto requestToDto(ForgotPasswordChangeRequest request);
}
