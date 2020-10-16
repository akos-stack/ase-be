package com.bloxico.userservice.entities.token;

import com.bloxico.userservice.services.token.ITokenService;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class RegistrationTokenFactory {

    public Token getInstance(ITokenService tokenService) {
        if (tokenService instanceof VerificationTokenServiceImpl) {
            return new VerificationToken();
        }

        if (tokenService instanceof PasswordTokenServiceImpl) {
            return new PasswordResetToken();
        }

        throw new EntityNotFoundException();
    }
}
