package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findAllByNameIgnoreCaseIn(Collection<String> names);

    default Role getUserRole() {
        return findByNameIgnoreCase(Role.USER).orElseThrow();
    }

    default Role getEvaluatorRole() {
        return findByNameIgnoreCase(Role.EVALUATOR).orElseThrow();
    }

    default Role getAdminRole() {
        return findByNameIgnoreCase(Role.ADMIN).orElseThrow();
    }

}
