package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SaveArtworkMetadataRequest implements IArtworkMetadataRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    String name;


    @JsonIgnore
    @Override
    public Status getStatus() {
        return APPROVED;
    }

    @NotNull
    @JsonProperty("type")
    @ApiModelProperty(required = true)
    Type type;

}
