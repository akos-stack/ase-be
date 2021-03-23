package com.bloxico.ase.userservice.web.model.token;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class HostInvitationRefreshTokenRequest {

    @NotBlank
    @ApiParam(name = "token", required = true)
    String token;

}
