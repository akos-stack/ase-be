package com.bloxico.userservice.util.tokencreator;

import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.repository.token.PasswordTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenCreatorUtil extends TokenCreatorUtil<PasswordResetToken> {

    public PasswordResetTokenCreatorUtil(PasswordTokenRepository passwordTokenRepository) {
        super(passwordTokenRepository);
    }
}
