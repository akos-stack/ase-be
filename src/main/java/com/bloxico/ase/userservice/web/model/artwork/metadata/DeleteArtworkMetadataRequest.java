package com.bloxico.ase.userservice.web.model.artwork.metadata;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DeleteArtworkMetadataRequest {

    @NotNull
    @ApiParam(name = "type", required = true)
    Type type;

    @NotNull
    @ApiParam(name = "name", required = true)
    String name;

}
