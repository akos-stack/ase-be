package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import com.bloxico.ase.userservice.validator.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UpdateArtworkDataRequest {

    @NotNull
    @JsonProperty("artwork_id")
    @ApiModelProperty(required = true)
    Long artworkId;

    @NotNull
    @JsonProperty("principal_image_id")
    @ApiModelProperty(required = true)
    Long principalImageId;

    @NotNull
    @NotEmpty
    @JsonProperty("title")
    @ApiModelProperty(required = true)
    String title;

    @NotNull
    @NotEmpty
    @JsonProperty("categories")
    @ApiModelProperty(required = true)
    List<String> categories;

    @NotNull
    @NotEmpty
    @JsonProperty("artist")
    @ApiModelProperty(required = true)
    String artist;

    @NotNull
    @JsonProperty("i_am_art_owner")
    @ApiModelProperty(required = true)
    Boolean iAmArtOwner;

    @NotNull
    @JsonProperty("year")
    @ApiModelProperty(required = true)
    Integer year;

    @NotNull
    @NotEmpty
    @JsonProperty("materials")
    @ApiModelProperty(required = true)
    List<String> materials;

    @NotNull
    @NotEmpty
    @JsonProperty("mediums")
    @ApiModelProperty(required = true)
    List<String> mediums;

    @NotNull
    @NotEmpty
    @JsonProperty("styles")
    @ApiModelProperty(required = true)
    List<String> styles;

    @NotNull
    @JsonProperty("weight")
    @ApiModelProperty(required = true)
    BigDecimal weight;

    @NotNull
    @JsonProperty("height")
    @ApiModelProperty(required = true)
    BigDecimal height;

    @NotNull
    @JsonProperty("width")
    @ApiModelProperty(required = true)
    BigDecimal width;

    @NotNull
    @JsonProperty("depth")
    @ApiModelProperty(required = true)
    BigDecimal depth;

    @NotNull
    @NotEmpty
    @JsonProperty("address")
    @ApiModelProperty(required = true)
    String address;

    @NullOrNotBlank
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

    @NullOrNotBlank
    @JsonProperty("appraisal_history")
    String appraisalHistory;

    @NullOrNotBlank
    @JsonProperty("location_history")
    String locationHistory;

    @NullOrNotBlank
    @JsonProperty("runs_history")
    String runsHistory;

    @NullOrNotBlank
    @JsonProperty("maintenance_history")
    String maintenanceHistory;

    @NullOrNotBlank
    @JsonProperty("notes")
    String notes;

    @NotNull
    @JsonProperty("status")
    @ApiModelProperty(required = true)
    Status status;

}
