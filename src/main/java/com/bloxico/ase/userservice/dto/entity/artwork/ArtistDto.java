package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ArtistDto extends BaseEntityDto {

    @JsonProperty("name")
    private String name;

}
