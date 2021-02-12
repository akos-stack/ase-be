package com.bloxico.ase.userservice.web.model.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class CreateRegionRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    String name;

}