package com.bloxico.ase.userservice.web.model.user;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class PagedUserDataResponse {

    @JsonProperty("users")
    @ApiModelProperty(required = true)
    List<UserDto> users;

    @JsonProperty("page_size")
    @ApiModelProperty(required = true)
    long pageElements;

    @JsonProperty("total_elements")
    @ApiModelProperty(required = true)
    long totalElements;

    @JsonProperty("total_pages")
    @ApiModelProperty(required = true)
    long totalPages;

}
