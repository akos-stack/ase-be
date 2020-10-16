package com.bloxico.userservice.web.model.password;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordInitRequest {

    @ValidEmail
    @NotNull
    private String email;
}
