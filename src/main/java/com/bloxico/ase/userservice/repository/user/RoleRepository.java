package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
}
