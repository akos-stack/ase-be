package com.bloxico.ase.userservice.web.model.config;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchConfigResponse {

    @JsonProperty("config")
    ConfigDto config;

}
