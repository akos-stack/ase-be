package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluatorDocument;
import com.bloxico.ase.userservice.entity.token.PendingEvaluatorDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingEvaluatorDocumentRepository extends JpaRepository<PendingEvaluatorDocument, PendingEvaluatorDocumentId> {

    Optional<PendingEvaluatorDocument> findByPendingEvaluatorDocumentId_Email(String email);
}
