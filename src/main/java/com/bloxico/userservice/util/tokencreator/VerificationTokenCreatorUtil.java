package com.bloxico.userservice.util.tokencreator;

import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.repository.token.VerificationTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class VerificationTokenCreatorUtil extends TokenCreatorUtil<VerificationToken> {

    public VerificationTokenCreatorUtil(VerificationTokenRepository verificationTokenRepository) {
        super(verificationTokenRepository);
    }
}
