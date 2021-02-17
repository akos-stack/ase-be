package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ArtworkGroupDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("artworks")
    private Set<Artwork> artworks = new HashSet<>();
}
