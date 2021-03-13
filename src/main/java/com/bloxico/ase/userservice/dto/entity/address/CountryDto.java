package com.bloxico.ase.userservice.dto.entity.address;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class CountryDto extends BaseEntityDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("regions")
    private Set<RegionDto> regions = new HashSet<>();

    @JsonIgnore
    public void addAllRegions(Collection<RegionDto> regions) {
        this.regions.addAll(regions);
    }

}
