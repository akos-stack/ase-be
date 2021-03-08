package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "name")
public class CountryDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("regions")
    private Set<RegionDto> regions = new HashSet<>();

    @JsonIgnore
    public void addAllRegions(Collection<RegionDto> regions) {
        this.regions.addAll(regions);
    }

}
