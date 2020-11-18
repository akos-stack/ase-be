package com.bloxico.ase.userservice.web.model.token;

import com.bloxico.userservice.util.validator.email.ValidEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class TokenValidationRequest {

    @NotNull
    @NotEmpty
    @ValidEmail
    @JsonProperty("email")
    String email;

    @NotNull
    @NotEmpty
    @JsonProperty("token_value")
    String tokenValue;

}
