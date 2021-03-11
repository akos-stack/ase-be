package com.bloxico.ase.userservice.web.model.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    @NotNull
    @JsonProperty("file_category")
    @ApiModelProperty(required = true)
    FileCategory fileCategory;

    @Override
    public String toString() {
        var fileNames = new ArrayList<String>();
        for (MultipartFile file : files) { fileNames.add(file.getOriginalFilename()); }
        return "ValidateFilesRequest{" +
                "files=" + fileNames +
                ", fileCategory=" + fileCategory +
                '}';
    }


}
