package com.bloxico.ase.userservice.util.mapper;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JwtToDtoMapper {

    JwtToDtoMapper INSTANCE = Mappers.getMapper(JwtToDtoMapper.class);

    default DecodedJwtDto decodedJwt(DecodedJWT decodedJWT) {
        return new DecodedJwtDto(
                decodedJWT.getClaim("id").asLong(),
                decodedJWT.getClaim("roles").asList(String.class),
                decodedJWT.getClaim("permissions").asList(String.class));
    }

}
