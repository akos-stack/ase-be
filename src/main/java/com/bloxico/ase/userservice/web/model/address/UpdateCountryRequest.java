package com.bloxico.ase.userservice.web.model.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateCountryRequest {

    @NotNull
    @JsonProperty("id")
    @ApiModelProperty(required = true)
    Integer id;

    @NotBlank
    @JsonProperty("country")
    @ApiModelProperty(required = true)
    String country;

    @JsonProperty("regions")
    @ApiModelProperty(required = true)
    @Size(min = 1)
    Set<String> regions;

}
