package com.bloxico.ase.userservice.web.model.user;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class RefreshRegistrationTokenRequest {

    @NotBlank
    @ApiParam(name = "token", required = true)
    String token;

}
