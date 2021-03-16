package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluatorDocument;
import com.bloxico.ase.userservice.entity.token.PendingEvaluatorDocument.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingEvaluatorDocumentRepository extends JpaRepository<PendingEvaluatorDocument, Id> {

    Optional<PendingEvaluatorDocument> findByIdEmail(String email);

}
