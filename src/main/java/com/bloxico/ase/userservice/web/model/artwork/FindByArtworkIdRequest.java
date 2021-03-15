package com.bloxico.ase.userservice.web.model.artwork;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class FindByArtworkIdRequest {

    @NotNull
    @ApiParam(name = "id")
    Long id;

}