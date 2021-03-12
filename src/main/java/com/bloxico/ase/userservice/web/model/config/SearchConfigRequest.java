package com.bloxico.ase.userservice.web.model.config;

import com.bloxico.ase.userservice.entity.config.Config.Type;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchConfigRequest {

    @NotNull
    @ApiParam(name = "type", required = true)
    Type type;

}
