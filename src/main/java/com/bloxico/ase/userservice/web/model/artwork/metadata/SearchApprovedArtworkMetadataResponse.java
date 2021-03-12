package com.bloxico.ase.userservice.web.model.artwork.metadata;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchApprovedArtworkMetadataResponse {

    @JsonProperty("entries")
    List<ArtworkMetadataDto> entries;

}
