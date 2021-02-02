package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RegionDataResponse {

    @JsonProperty("region")
    RegionDto region;

}
