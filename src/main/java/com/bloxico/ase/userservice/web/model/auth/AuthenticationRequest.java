package com.bloxico.ase.userservice.web.model.auth;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import com.bloxico.userservice.util.validator.password.RegularPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class AuthenticationRequest {

    @NotNull
    @ValidEmail
    @JsonProperty("email")
    String email;

    @NotNull
    @RegularPassword
    @JsonProperty("password")
    String password;

}
