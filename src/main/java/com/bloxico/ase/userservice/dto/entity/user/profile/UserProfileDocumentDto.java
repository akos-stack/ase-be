package com.bloxico.ase.userservice.dto.entity.user.profile;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class UserProfileDocumentDto extends BaseEntityAuditDto {

    @JsonProperty("user_profile_id")
    Long userProfileId;

    @JsonProperty("document_id")
    Long documentId;

}