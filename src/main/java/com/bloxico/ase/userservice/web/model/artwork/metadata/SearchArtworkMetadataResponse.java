package com.bloxico.ase.userservice.web.model.artwork.metadata;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchArtworkMetadataResponse {

    @JsonProperty("page")
    ResponsePage<ArtworkMetadataDto> page;

    public SearchArtworkMetadataResponse(Page<ArtworkMetadataDto> page) {
        this.page = new ResponsePage<>(page);
    }

}
