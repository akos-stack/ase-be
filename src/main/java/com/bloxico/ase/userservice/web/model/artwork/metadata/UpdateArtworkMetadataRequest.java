package com.bloxico.ase.userservice.web.model.artwork.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateArtworkMetadataRequest implements IArtworkMetadataRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    String name;

    @JsonProperty("status")
    @ApiModelProperty(required = true)
    Status status;

    @JsonProperty("type")
    @ApiModelProperty(required = true)
    Type type;

}
