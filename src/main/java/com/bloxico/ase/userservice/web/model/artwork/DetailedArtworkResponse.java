package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DetailedArtworkResponse {

    @JsonProperty("artwork")
    ArtworkDto artwork;

    @JsonProperty("location")
    LocationDto location;

    @JsonProperty("documents")
    List<DocumentDto> documents;

}
