package com.bloxico.userservice.web.model.password;

import lombok.Data;

@Data
public class ConfirmPasswordLinkResponse {
    String tokenValue;

    public ConfirmPasswordLinkResponse(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
