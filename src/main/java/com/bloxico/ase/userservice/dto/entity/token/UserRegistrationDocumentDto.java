package com.bloxico.ase.userservice.dto.entity.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserRegistrationDocumentDto {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("document_id")
    private Long documentId;

}
