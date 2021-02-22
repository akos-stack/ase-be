package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class ArtworkDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("artist")
    private ArtistDto artist;

    @JsonProperty("owner")
    private ArtOwnerDto owner;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("weight")
    private BigDecimal weight;

    @JsonProperty("height")
    private BigDecimal height;

    @JsonProperty("width")
    private BigDecimal width;

    @JsonProperty("depth")
    private BigDecimal depth;

    @JsonProperty("phone_number")
    private String phone;

    @JsonProperty("location")
    private LocationDto location;

    @JsonProperty("group")
    private ArtworkGroupDto group;

    @JsonProperty("history")
    private ArtworkHistoryDto history;

    @JsonProperty("categories")
    private Set<ArtworkMetadataDto> categories = new HashSet<>();

    @JsonProperty("materials")
    private Set<ArtworkMetadataDto> materials = new HashSet<>();

    @JsonProperty("mediums")
    private Set<ArtworkMetadataDto> mediums = new HashSet<>();

    @JsonProperty("styles")
    private Set<ArtworkMetadataDto> styles = new HashSet<>();

    @JsonProperty("documents")
    private Set<DocumentDto> documents = new HashSet<>();

    public void addDocuments(Collection<DocumentDto> documentDto) {
        documents.addAll(documentDto);
    }

    public void addStyles(Collection<ArtworkMetadataDto> artworkMetadataDto) {
        styles.addAll(artworkMetadataDto);
    }

    public void addMediums(Collection<ArtworkMetadataDto> artworkMetadataDto) {
        mediums.addAll(artworkMetadataDto);
    }

    public void addMaterials(Collection<ArtworkMetadataDto> artworkMetadataDto) {
        materials.addAll(artworkMetadataDto);
    }

    public void addCategories(Collection<ArtworkMetadataDto> artworkMetadataDto) {
        categories.addAll(artworkMetadataDto);
    }

}
