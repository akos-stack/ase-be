package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class ArtworkMetadataDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private Status status;

}
