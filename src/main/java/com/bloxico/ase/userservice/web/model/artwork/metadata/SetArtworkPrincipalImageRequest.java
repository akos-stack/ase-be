package com.bloxico.ase.userservice.web.model.artwork.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SetArtworkPrincipalImageRequest {

    @NotNull
    @JsonProperty("artwork_id")
    @ApiModelProperty(required = true)
    Long artworkId;

    @NotNull
    @JsonProperty("document_id")
    @ApiModelProperty(required = true)
    Long documentId;

}
