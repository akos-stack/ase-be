package com.bloxico.ase.userservice.dto.entity.artwork;

import com.bloxico.ase.userservice.dto.entity.BaseEntityAuditDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArtworkDocumentDto extends BaseEntityAuditDto {

    @JsonProperty("artwork_id")
    Long artworkId;

    @JsonProperty("document_id")
    Long documentId;

}
