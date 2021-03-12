package com.bloxico.ase.userservice.dto.entity.document;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "path", callSuper = false)
public class DocumentDto extends BaseEntityDto {

    @JsonProperty("path")
    private String path;

    @JsonProperty("type")
    private FileCategory type;
}
