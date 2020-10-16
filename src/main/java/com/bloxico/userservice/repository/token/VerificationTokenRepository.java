package com.bloxico.userservice.repository.token;

import com.bloxico.userservice.entities.token.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends TokenRepository<VerificationToken> {
}
