package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class ArtworkMetadataCreateRequest implements IArtworkMetadataRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    String name;


    @JsonIgnore
    @Override
    public ArtworkMetadataStatus getStatus() {
        return APPROVED;
    }

    @JsonProperty("type")
    @ApiModelProperty(required = true)
    ArtworkMetadataType type;

}