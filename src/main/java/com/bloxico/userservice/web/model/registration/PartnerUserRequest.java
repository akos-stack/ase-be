package com.bloxico.userservice.web.model.registration;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PartnerUserRequest {

    @NotNull
    @NotEmpty
    private String email;
}
