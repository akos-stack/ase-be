package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import java.util.List;

@Value
public class SearchArtworkMetadataResponse {

    @JsonProperty("entries")
    @ApiModelProperty(required = true)
    List<ArtworkMetadataDto> entries;
}
