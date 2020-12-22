package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingEvaluatorRepository extends JpaRepository<PendingEvaluator, String> {
}
