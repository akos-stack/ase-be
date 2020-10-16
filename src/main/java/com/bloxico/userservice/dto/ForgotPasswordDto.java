package com.bloxico.userservice.dto;

import lombok.Data;

@Data
public class ForgotPasswordDto {

    private String email;
    private String tokenValue;
    private String newPassword;
}
