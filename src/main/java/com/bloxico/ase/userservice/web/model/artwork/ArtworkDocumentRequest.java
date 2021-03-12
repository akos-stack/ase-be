package com.bloxico.ase.userservice.web.model.artwork;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class ArtworkDocumentRequest {

    @NotNull
    @ApiParam(name = "artworkId", required = true)
    Long artworkId;

    @NotNull
    @ApiParam(name = "documentId", required = true)
    Long documentId;

}
