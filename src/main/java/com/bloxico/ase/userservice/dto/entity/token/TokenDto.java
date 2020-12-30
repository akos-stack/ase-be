package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.entity.token.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "value")
public class TokenDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("type")
    private Token.Type type;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

}
