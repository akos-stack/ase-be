package com.bloxico.ase.userservice.web.model.s3;

import com.bloxico.ase.userservice.util.FileCategory;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateFilesRequest {

    @NotNull
    @ApiParam(name = "category", required = true)
    private FileCategory category;

    @NotNull
    @NotEmpty
    @ApiParam(name = "category", required = true)
    private List<MultipartFile> files;

}
