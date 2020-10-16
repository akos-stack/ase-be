package com.bloxico.userservice.dto.entities;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(exclude = "tokenValue")
public class TokenDto {
    private Long id;
    private String tokenValue;
    private Long userId;
    private Date expiryDate;
}
