package com.bloxico.ase.userservice.web.model.address;

import com.bloxico.ase.userservice.projection.RegionDetailsProj;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class SearchRegionsResponse {

    @JsonProperty("regions")
    List<RegionDetailsProj> regions;

    @JsonProperty("total_elements")
    long totalElements;

}
