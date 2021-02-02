package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CountryDataResponse {

    @JsonProperty("country")
    CountryDto country;

}
