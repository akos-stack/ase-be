package com.bloxico.ase.userservice.web.model.config;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveConfigResponse {

    @JsonProperty("config")
    ConfigDto config;

}
