package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ArtworkGroupDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("status")
    private ArtworkGroup.Status status;
}
