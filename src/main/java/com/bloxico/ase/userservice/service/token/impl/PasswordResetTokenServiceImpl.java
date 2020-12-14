package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;

@Service
public class PasswordResetTokenServiceImpl extends AbstractTokenServiceImpl {

    @Autowired
    public PasswordResetTokenServiceImpl(TokenRepository tokenRepository) {
        super(tokenRepository);
    }

    @Override
    protected Token.Type getType() {
        return PASSWORD_RESET;
    }

}
