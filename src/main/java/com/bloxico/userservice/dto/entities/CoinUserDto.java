package com.bloxico.userservice.dto.entities;

import lombok.Data;

@Data
public class CoinUserDto {

    private Long id;
    private String email;

    private boolean hasPassword;
}
