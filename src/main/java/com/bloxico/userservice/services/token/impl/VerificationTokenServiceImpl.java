package com.bloxico.userservice.services.token.impl;

import com.bloxico.userservice.entities.token.RegistrationTokenFactory;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.repository.token.TokenRepository;
import com.bloxico.userservice.util.tokencreator.VerificationTokenCreatorUtil;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl extends TokenServiceImpl<VerificationToken> {

    public VerificationTokenServiceImpl(TokenRepository<VerificationToken> tokenRepository,
                                        VerificationTokenCreatorUtil tokenCreatorUtil,
                                        RegistrationTokenFactory registrationTokenFactory) {
        super(tokenRepository, tokenCreatorUtil, registrationTokenFactory);
    }

}
