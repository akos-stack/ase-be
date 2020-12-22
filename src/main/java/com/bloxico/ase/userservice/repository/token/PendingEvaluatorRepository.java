package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingEvaluatorRepository extends JpaRepository<PendingEvaluator, String> {

    Optional<PendingEvaluator> findByEmailIgnoreCase(String email);

    Optional<PendingEvaluator> findByEmailIgnoreCaseAndToken(String email, String token);

}
