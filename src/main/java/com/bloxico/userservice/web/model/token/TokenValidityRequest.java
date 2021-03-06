package com.bloxico.userservice.web.model.token;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * This request is being sent when user needs to validate 6 digit code assigned to him.
 */
@Data
public class TokenValidityRequest {

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String tokenValue;

}
