package com.bloxico.ase.userservice.dto.entity.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PendingEvaluatorDocumentDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("document_id")
    private Long documentId;
}
