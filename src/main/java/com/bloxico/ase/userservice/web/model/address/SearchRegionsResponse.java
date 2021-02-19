package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchRegionsResponse {

    @JsonProperty("regions")
    List<RegionDto> regions;

}
