package com.bloxico.userservice.util.mappers;

import com.bloxico.userservice.dto.entities.*;
import com.bloxico.userservice.entities.token.Token;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.Region;
import com.bloxico.userservice.entities.user.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EntityDataMapper {

    EntityDataMapper INSTANCE = Mappers.getMapper(EntityDataMapper.class);

    RegionDto regionToDto(Region region);

    List<RegionDto> regionsToDtos(List<Region> regions);

    @Mappings({
            @Mapping(expression = "java(coinUser.getPassword() != null)", target = "hasPassword"),
    })
    CoinUserDto coinUserToDto(CoinUser coinUser);

    @Mappings({
            @Mapping(source = "userProfile.coinUser.email", target = "email"),
            @Mapping(source = "userProfile.region.regionName", target = "region")
    })
    UserProfileDto userProfileToDto(UserProfile userProfile);

    TokenDto tokenToDto(Token token);
}
