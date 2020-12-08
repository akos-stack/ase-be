package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AseMapper {

    AseMapper MAPPER = Mappers.getMapper(AseMapper.class);

    UserProfile toUserProfile(RegistrationRequest request);

    UserProfileDto toUserProfileDto(UserProfile userProfile);

    UserProfile toUserProfile(UserProfileDto userProfile);

    RoleDto toRoleDto(Role role);

    @Mapping(target = "token", source = "tokenId")
    BlacklistedToken toBlacklistedToken(OAuthAccessTokenDto tokens);

}
