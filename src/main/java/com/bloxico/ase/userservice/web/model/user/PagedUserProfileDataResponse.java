package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class PagedUserProfileDataResponse {

    @JsonProperty("user_profiles")
    @ApiModelProperty(required = true)
    List<UserProfileDto> userProfiles;

    @JsonProperty("pageSize")
    @ApiModelProperty(required = true)
    long pageElements;

    @JsonProperty("totalElements")
    @ApiModelProperty(required = true)
    long totalElements;

    @JsonProperty("totalPages")
    @ApiModelProperty(required = true)
    long totalPages;

}
