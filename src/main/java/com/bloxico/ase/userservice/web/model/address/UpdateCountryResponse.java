package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateCountryResponse {

    @JsonProperty("country")
    CountryDto country;

}
