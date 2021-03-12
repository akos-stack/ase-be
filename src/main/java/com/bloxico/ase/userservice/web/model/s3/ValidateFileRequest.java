package com.bloxico.ase.userservice.web.model.s3;

import com.bloxico.ase.userservice.util.FileCategory;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateFileRequest {

    @NotNull
    @ApiParam(name = "category", required = true)
    FileCategory category;

    @NotNull
    @ApiParam(name = "file", required = true)
    MultipartFile file;

}
