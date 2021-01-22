package com.bloxico.ase.userservice.repository.aspiration;

import com.bloxico.ase.userservice.entity.aspiration.Aspiration;
import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AspirationRepository extends JpaRepository<Aspiration, Short> {

    List<Aspiration> findAllByRoleNameIn(Collection<String> names);

    Optional<Aspiration> findByRoleName(String name);

    default Aspiration getUserAspiration() {
        return findByRoleName(Role.USER).orElseThrow();
    }

    default Aspiration getEvaluatorAspiration() {
        return findByRoleName(Role.EVALUATOR).orElseThrow();
    }

}
