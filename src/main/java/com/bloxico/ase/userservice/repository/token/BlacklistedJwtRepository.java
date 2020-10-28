package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedJwtRepository extends JpaRepository<BlacklistedJwt, String> {
}
