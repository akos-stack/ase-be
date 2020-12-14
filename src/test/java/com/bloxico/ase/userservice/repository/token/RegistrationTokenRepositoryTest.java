package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.Token;

import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;

public class RegistrationTokenRepositoryTest extends TokenRepositoryTest {

    @Override
    protected Token.Type tokenType() {
        return REGISTRATION;
    }

}
