package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.Evaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluatorRepository extends JpaRepository<Evaluator, Long> {

    Optional<Evaluator> findByUserProfile_UserId(Long id);

}
