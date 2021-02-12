package com.bloxico.ase.userservice.web.model.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class RegistrationResponse {

    @JsonProperty("token_value")
    String tokenValue;

}
