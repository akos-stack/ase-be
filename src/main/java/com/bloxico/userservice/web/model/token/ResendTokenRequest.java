package com.bloxico.userservice.web.model.token;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ResendTokenRequest {

    @NotEmpty
    @NotNull
    private String email;
}
