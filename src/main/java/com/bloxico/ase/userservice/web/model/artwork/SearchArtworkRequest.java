package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchArtworkRequest {

    @ApiParam(name = "status")
    Artwork.Status status;

    @ApiParam(name = "title")
    String title;

}
