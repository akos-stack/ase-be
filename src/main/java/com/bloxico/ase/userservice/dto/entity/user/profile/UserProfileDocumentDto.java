package com.bloxico.ase.userservice.dto.entity.user.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserProfileDocumentDto {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("document_id")
    private Long documentId;

}