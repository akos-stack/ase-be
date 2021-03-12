package com.bloxico.ase.userservice.dto.entity.address;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class RegionDto extends BaseEntityDto {

    @JsonProperty("name")
    private String name;

}
