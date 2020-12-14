package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.entity.token.Token;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "value")
public class TokenDto {

    private Long id;
    private String value;
    private Token.Type type;
    private Long userId;
    private LocalDateTime expiryDate;

}
