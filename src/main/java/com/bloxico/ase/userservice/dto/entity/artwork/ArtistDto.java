package com.bloxico.ase.userservice.dto.entity.artwork;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ArtistDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

}
