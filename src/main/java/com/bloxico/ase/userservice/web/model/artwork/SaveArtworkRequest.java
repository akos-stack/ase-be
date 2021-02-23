package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_MISSING_CERTIFICATE;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_MISSING_RESUME;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@Getter
@Setter
public class SaveArtworkRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("images")
    @ApiModelProperty(required = true)
    List<MultipartFile> images;

    @NotNull
    @NotEmpty
    @JsonProperty("principal_image")
    @ApiModelProperty(required = true)
    MultipartFile principalImage;

    @NotNull
    @NotEmpty
    @JsonProperty("title")
    @ApiModelProperty(required = true)
    String title;

    @NotNull
    @NotEmpty
    @JsonProperty("categories")
    @ApiModelProperty(required = true)
    String[] categories;

    @JsonProperty("artist")
    @ApiModelProperty(required = true)
    String artist;

    @NotNull
    @NotEmpty
    @JsonProperty("iAmArtOwner")
    @ApiModelProperty(required = true)
    Boolean iAmArtOwner;

    @NotNull
    @NotEmpty
    @JsonProperty("year")
    @ApiModelProperty(required = true)
    Integer year;

    @NotNull
    @NotEmpty
    @JsonProperty("materials")
    @ApiModelProperty(required = true)
    String[] materials;

    @NotNull
    @NotEmpty
    @JsonProperty("mediums")
    @ApiModelProperty(required = true)
    String[] mediums;

    @NotNull
    @NotEmpty
    @JsonProperty("styles")
    @ApiModelProperty(required = true)
    String[] styles;

    @JsonProperty("document")
    @ApiModelProperty(required = true)
    MultipartFile document;

    @JsonProperty("fileCategory")
    @ApiModelProperty(required = true)
    FileCategory fileCategory;

    @NotNull
    @NotEmpty
    @JsonProperty("weight")
    @ApiModelProperty(required = true)
    BigDecimal weight;

    @NotNull
    @NotEmpty
    @JsonProperty("height")
    @ApiModelProperty(required = true)
    BigDecimal height;

    @NotNull
    @NotEmpty
    @JsonProperty("width")
    @ApiModelProperty(required = true)
    BigDecimal width;

    @NotNull
    @NotEmpty
    @JsonProperty("depth")
    @ApiModelProperty(required = true)
    BigDecimal depth;

    @NotNull
    @NotEmpty
    @JsonProperty("address")
    @ApiModelProperty(required = true)
    String address;

    @JsonProperty("address2")
    String address2;

    @NotNull
    @NotEmpty
    @JsonProperty("city")
    @ApiModelProperty(required = true)
    String city;

    @NotNull
    @NotEmpty
    @JsonProperty("country")
    @ApiModelProperty(required = true)
    String country;

    @NotNull
    @NotEmpty
    @JsonProperty("region")
    @ApiModelProperty(required = true)
    String region;

    @NotNull
    @NotEmpty
    @JsonProperty("zip_code")
    @ApiModelProperty(required = true)
    String zipCode;

    @NotNull
    @NotEmpty
    @JsonProperty("phone")
    @ApiModelProperty(required = true)
    String phone;

    @JsonProperty("appraisal_history")
    String appraisalHistory;

    @JsonProperty("location_history")
    String locationHistory;

    @JsonProperty("runs_history")
    String runsHistory;

    @JsonProperty("maintenance_history")
    String maintenanceHistory;

    @JsonProperty("notes")
    String notes;

    @NotNull
    @NotEmpty
    @JsonProperty("status")
    @ApiModelProperty(required = true)
    ArtworkGroup.Status status;

    @JsonProperty("groupId")
    @ApiModelProperty
    Long groupId;

    @JsonIgnore
    public void validateRequest() {
        if(iAmArtOwner && fileCategory != FileCategory.CV) {
            throw ARTWORK_MISSING_RESUME.newException();
        } else if(!iAmArtOwner && fileCategory != FileCategory.CERTIFICATE) {
            throw ARTWORK_MISSING_CERTIFICATE.newException();
        }
    }

    @JsonIgnore
    public boolean hasHistory() {
        return !StringUtils.isEmpty(appraisalHistory)
                || !StringUtils.isEmpty(locationHistory)
                || !StringUtils.isEmpty(runsHistory)
                || !StringUtils.isEmpty(maintenanceHistory)
                || !StringUtils.isEmpty(notes);
    }
}
