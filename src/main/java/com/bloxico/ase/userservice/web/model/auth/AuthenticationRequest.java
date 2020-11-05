package com.bloxico.ase.userservice.web.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class AuthenticationRequest {

    @JsonProperty("email")
    String email;

    @JsonProperty("password")
    String password;

}
