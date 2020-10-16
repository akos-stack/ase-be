package com.bloxico.userservice.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {

    private String oldPassword;
    private String newPassword;
}
