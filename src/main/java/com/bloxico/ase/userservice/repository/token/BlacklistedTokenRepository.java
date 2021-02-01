package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    @Query("SELECT DISTINCT t.value FROM BlacklistedToken t")
    List<String> findDistinctTokenValues();

    @Modifying
    @Query("DELETE FROM BlacklistedToken t WHERE t.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
