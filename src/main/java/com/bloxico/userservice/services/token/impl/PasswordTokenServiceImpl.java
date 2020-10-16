package com.bloxico.userservice.services.token.impl;

import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.entities.token.RegistrationTokenFactory;
import com.bloxico.userservice.repository.token.TokenRepository;
import com.bloxico.userservice.util.tokencreator.PasswordResetTokenCreatorUtil;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenServiceImpl extends TokenServiceImpl<PasswordResetToken> {

    public PasswordTokenServiceImpl(TokenRepository<PasswordResetToken> tokenRepository,
                                    PasswordResetTokenCreatorUtil passwordResetTokenCreatorUtil,
                                    RegistrationTokenFactory registrationTokenFactory) {
        super(tokenRepository, passwordResetTokenCreatorUtil, registrationTokenFactory);
    }
}
