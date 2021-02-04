package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.Evaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluatorRepository extends JpaRepository<Evaluator, Long> {
}
