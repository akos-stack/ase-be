package com.bloxico.ase.userservice.entity.token;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "pending_evaluators_documents")
public class PendingEvaluatorDocument implements Serializable {

    @EmbeddedId
    private PendingEvaluatorDocumentId pendingEvaluatorDocumentId;
}
