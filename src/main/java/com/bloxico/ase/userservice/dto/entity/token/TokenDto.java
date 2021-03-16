package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.entity.token.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "value", callSuper = false)
public class TokenDto extends BaseEntityDto {

    @JsonProperty("value")
    private String value;

    @JsonProperty("type")
    private Token.Type type;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("expiry_date")
    private LocalDateTime expiryDate;

}
