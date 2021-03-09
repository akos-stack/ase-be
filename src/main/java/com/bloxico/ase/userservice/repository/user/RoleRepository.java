package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.User.ROLE_NOT_FOUND;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findAllByNameIgnoreCaseIn(Collection<String> names);

    default Role getRole(String name) {
        return findByNameIgnoreCase(name)
                .orElseThrow(ROLE_NOT_FOUND::newException);
    }

    default Role getUserRole() {
        return findByNameIgnoreCase(Role.USER)
                .orElseThrow();
    }

    default Role getEvaluatorRole() {
        return findByNameIgnoreCase(Role.EVALUATOR)
                .orElseThrow();
    }

    default Role getAdminRole() {
        return findByNameIgnoreCase(Role.ADMIN)
                .orElseThrow();
    }

}
