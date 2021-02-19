package com.bloxico.ase.userservice.web.model.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateCountryRequest {

    @NotBlank
    @JsonProperty("country")
    @ApiModelProperty(required = true)
    String country;

    @NotBlank
    @JsonProperty("region")
    @ApiModelProperty(required = true)
    String region;

}
