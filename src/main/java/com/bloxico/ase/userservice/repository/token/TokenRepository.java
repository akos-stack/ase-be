package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValue(String token);

    Optional<Token> findByTypeAndUserId(Token.Type type, long userId);

    @Query("SELECT t FROM Token t WHERE t.type = ?1 AND t.expiryDate < CURRENT_TIMESTAMP")
    List<Token> findAllExpiredTokensByType(Token.Type type);

}
