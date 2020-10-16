package com.bloxico.userservice.web.model.registration;

import lombok.Data;

@Data
public class RegistrationResponse {

    String tokenValue;

    public RegistrationResponse(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
