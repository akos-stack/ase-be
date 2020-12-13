package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.Token;

import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;

public class PasswordResetTokenRepositoryTest extends TokenRepositoryTest {

    @Override
    protected Token.Type tokenType() {
        return PASSWORD_RESET;
    }

}
