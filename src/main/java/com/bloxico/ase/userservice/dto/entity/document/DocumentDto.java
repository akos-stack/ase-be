package com.bloxico.ase.userservice.dto.entity.document;

import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class DocumentDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("path")
    private String path;

    @JsonProperty("type")
    private FileCategory type;
}
