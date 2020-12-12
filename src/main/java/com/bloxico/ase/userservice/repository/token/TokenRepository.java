package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValue(String token);

    Optional<Token> findByTypeAndUserId(Token.Type type, long userId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

}
