package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"country", "name"})
public class CityDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("country")
    private CountryDto country;

    @JsonProperty("name")
    private String name;

    @JsonProperty("zip_code")
    private String zipCode;

}
