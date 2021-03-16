package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class PendingEvaluatorDocumentDto extends BaseEntityAuditDto {

    @JsonProperty("email")
    String email;

    @JsonProperty("document_id")
    Long documentId;

}
