package com.bloxico.userservice.web.model.registration;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import com.bloxico.userservice.util.validator.password.RegularPassword;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString(exclude = {"password", "matchPassword"})
public class RegistrationRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;

    @NotNull
    @NotEmpty
    @RegularPassword
    private String password;

    @NotNull
    @NotEmpty
    private String matchPassword;

    @NotNull
    @NotEmpty
    private String regionName;

    private String city;

    private String name;
}
