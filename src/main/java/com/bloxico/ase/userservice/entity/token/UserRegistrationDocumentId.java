package com.bloxico.ase.userservice.entity.token;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"user_id", "document_id"})
@Embeddable
public class UserRegistrationDocumentId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "document_id")
    private Long documentId;

}
