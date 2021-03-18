package com.bloxico.ase.userservice.web.model.address;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DeleteRegionRequest {

    @NotNull
    @ApiParam(name = "id", required = true)
    Long id;

}
