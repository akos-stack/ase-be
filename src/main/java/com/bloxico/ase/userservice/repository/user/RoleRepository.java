package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {

    Optional<Role> findByNameIgnoreCase(String name);

    default Role getUserRole() {
        return findByNameIgnoreCase(Role.USER).orElseThrow();
    }

}
