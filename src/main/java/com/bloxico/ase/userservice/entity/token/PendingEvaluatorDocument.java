package com.bloxico.ase.userservice.entity.token;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "pending_evaluators_documents")
public class PendingEvaluatorDocument implements Serializable {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "email")
        private String email;

        @Column(name = "document_id")
        private Long documentId;

    }

    @EmbeddedId
    private Id id;

}
