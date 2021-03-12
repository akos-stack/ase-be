package com.bloxico.ase.userservice.dto.entity.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.bloxico.ase.userservice.entity.config.Config.Type;

@Data
@EqualsAndHashCode(of = {"type", "value"})
public class ConfigDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("value")
    private String value;

}
