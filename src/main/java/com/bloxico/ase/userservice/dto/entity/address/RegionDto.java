package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class RegionDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

}
