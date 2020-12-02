package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    @Query("SELECT DISTINCT t.token FROM BlacklistedToken t")
    List<String> findDistinctTokenValues();

}
