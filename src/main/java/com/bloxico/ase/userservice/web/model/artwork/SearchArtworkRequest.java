package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import io.swagger.annotations.ApiParam;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchArtworkRequest {

    @ApiParam(name = "status")
    Status status;

    @ApiParam(name = "title")
    String title;

}
