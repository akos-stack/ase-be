package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveCountryResponse {

    @JsonProperty("country")
    CountryDto country;

}
