package com.bloxico.ase.userservice.dto.entity.token;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class PendingEvaluatorDto extends BaseEntityAuditDto {

    @JsonProperty("email")
    String email;

    @JsonProperty("token")
    String token;

    @JsonProperty("status")
    Status status;

    @JsonProperty("document")
    DocumentDto document;

}
