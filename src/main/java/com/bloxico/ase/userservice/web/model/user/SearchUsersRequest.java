package com.bloxico.ase.userservice.web.model.user;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchUsersRequest {

    @NotNull
    @ApiParam(name = "email", required = true)
    String email;

    @ApiParam(name = "role")
    String role;

}
