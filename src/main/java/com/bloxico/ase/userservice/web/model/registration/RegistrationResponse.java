package com.bloxico.ase.userservice.web.model.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RegistrationResponse {

    @JsonProperty("token_value")
    String tokenValue;

}
