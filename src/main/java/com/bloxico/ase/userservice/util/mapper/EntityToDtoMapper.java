package com.bloxico.ase.userservice.util.mapper;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EntityToDtoMapper {

    EntityToDtoMapper INSTANCE = Mappers.getMapper(EntityToDtoMapper.class);

    UserProfileDto userProfile(UserProfile userProfile);

}
