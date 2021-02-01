package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class PagedUserDataResponse {

    @JsonProperty("users")
    @ApiModelProperty(required = true)
    List<UserDto> users;

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
