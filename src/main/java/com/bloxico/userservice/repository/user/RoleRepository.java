package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.CoinRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("roleRepositoryOld")
public interface RoleRepository extends JpaRepository<CoinRole, Long> {

    Optional<CoinRole> findRoleByRoleName(CoinRole.RoleName roleName);

}
