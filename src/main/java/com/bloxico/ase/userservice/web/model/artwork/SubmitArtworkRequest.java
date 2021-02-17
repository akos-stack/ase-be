package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@Getter
@Setter
public class SubmitArtworkRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("images")
    @ApiModelProperty(required = true)
    List<MultipartFile> images;

    @NotNull
    @NotEmpty
    @JsonProperty("principal_picture")
    @ApiModelProperty(required = true)
    MultipartFile principalPicture;

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

    @JsonProperty("cv")
    MultipartFile cv;

    @JsonProperty("certificate")
    MultipartFile certificate;

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

    @JsonProperty("maintenance_records")
    String maintenanceRecords;

    @JsonProperty("notes")
    String notes;

    @NotNull
    @NotEmpty
    @JsonProperty("status")
    @ApiModelProperty(required = true)
    ArtworkStatus status;

    @JsonProperty("group_id")
    @ApiModelProperty
    Long groupId;
}
