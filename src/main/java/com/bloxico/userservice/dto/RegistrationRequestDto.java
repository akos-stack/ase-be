package com.bloxico.userservice.dto;

import lombok.Data;
import lombok.ToString;

@ToString(exclude = {"password", "matchPassword"})
@Data
public class RegistrationRequestDto {

    private String email;
    private String password;
    private String matchPassword;

    private String regionName;
    private String city;

    private String name;
}
