package com.bloxico.ase.userservice.web.model.config;

import com.bloxico.ase.userservice.entity.config.Config.Type;
import com.bloxico.ase.userservice.validator.ValidConfigRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@ValidConfigRequest
public class SaveConfigRequest {

    @NotNull
    @JsonProperty("type")
    Type type;

    @NotNull
    @JsonProperty("value")
    Object value;

}
