package com.bloxico.ase.userservice.entity.token;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"email", "documentId"})
@Embeddable
public class PendingEvaluatorDocumentId implements Serializable {

    @Column(name = "email")
    private String email;

    @Column(name = "document_id")
    private Long documentId;

}
