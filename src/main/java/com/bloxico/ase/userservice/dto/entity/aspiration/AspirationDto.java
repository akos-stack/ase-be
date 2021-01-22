package com.bloxico.ase.userservice.dto.entity.aspiration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class AspirationDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

}
