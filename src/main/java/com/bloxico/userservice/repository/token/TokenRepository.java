package com.bloxico.userservice.repository.token;

import com.bloxico.userservice.entities.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {
    Optional<T> findByTokenValue(String tokenValue);

    Optional<T> findByUserId(long userId);

    @Query("select e FROM #{#entityName} e WHERE e.userId = ?1 AND e.expiryDate > CURRENT_TIMESTAMP")
    Optional<T> findNonExpiredByUserId(long userId);

    @Query("select e FROM #{#entityName} e WHERE e.expiryDate < LOCALTIMESTAMP")
    List<T> findExpiredTokens();

    @Modifying
    void deleteByIdIn(List<Long> idsToDelete);

}
