package com.bloxico.ase.userservice.entity.user.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"userProfileId", "documentId"})
@Embeddable
public class UserProfileDocumentId implements Serializable {

    @Column(name = "user_profile_id")
    private Long userProfileId;

    @Column(name = "document_id")
    private Long documentId;

}
