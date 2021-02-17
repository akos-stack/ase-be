package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SubmitArtworkResponse {

    @JsonProperty("artwork_group")
    @ApiModelProperty(required = true)
    ArtworkGroupDto groupDto;
}
