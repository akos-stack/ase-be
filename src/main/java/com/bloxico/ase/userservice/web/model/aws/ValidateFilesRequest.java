package com.bloxico.ase.userservice.web.model.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
@Getter
@Setter
public class ValidateFilesRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("files")
    @ApiModelProperty(required = true)
    List<MultipartFile> files;


    @JsonProperty("file_category")
    @ApiModelProperty(required = true)
    FileCategory fileCategory;
}
