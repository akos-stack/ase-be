package com.bloxico.ase.userservice.web.model.artwork.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchArtworkMetadataRequest {

    @NotNull
    @ApiParam(name = "type", required = true)
    Type type;

    @ApiParam(name = "status")
    Status status;

    @ApiParam(name = "name")
    String name;

}
