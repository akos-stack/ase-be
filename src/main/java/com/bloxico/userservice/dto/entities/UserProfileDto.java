package com.bloxico.userservice.dto.entities;

import lombok.Data;

@Data
public class UserProfileDto {
    private String email;
    private String name;
    private String city;
    private String region;
}
