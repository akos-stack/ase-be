package com.bloxico.userservice.repository.token;

import com.bloxico.userservice.entities.token.PasswordResetToken;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends TokenRepository<PasswordResetToken> {
}
