package com.bloxico.ase.userservice.dto.entity.artwork.metadata;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class ArtworkMetadataDto extends BaseEntityDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private Status status;

}
