package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArtworkDto extends BaseEntityDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("artist")
    private ArtistDto artist;

    @JsonProperty("owner_id")
    private Long ownerId;

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

    @JsonProperty("location_id")
    private Long locationId;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("appraisal_history")
    private String appraisalHistory;

    @JsonProperty("location_history")
    private String locationHistory;

    @JsonProperty("runs_history")
    private String runsHistory;

    @JsonProperty("maintenance_history")
    private String maintenanceHistory;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("categories")
    private Set<ArtworkMetadataDto> categories = new HashSet<>();

    @JsonProperty("materials")
    private Set<ArtworkMetadataDto> materials = new HashSet<>();

    @JsonProperty("mediums")
    private Set<ArtworkMetadataDto> mediums = new HashSet<>();

    @JsonProperty("styles")
    private Set<ArtworkMetadataDto> styles = new HashSet<>();

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
