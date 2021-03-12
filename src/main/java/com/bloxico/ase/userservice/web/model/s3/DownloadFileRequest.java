package com.bloxico.ase.userservice.web.model.s3;

import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class DownloadFileRequest {

    @NotBlank
    @ApiParam(name = "path", required = true)
    String path;

}
