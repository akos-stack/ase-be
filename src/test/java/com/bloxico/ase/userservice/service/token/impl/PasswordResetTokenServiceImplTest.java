package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.Token;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;

public class PasswordResetTokenServiceImplTest extends AbstractTokenServiceImplTest {

    @Autowired
    private PasswordResetTokenServiceImpl service;

    @Override
    protected Token.Type tokenType() {
        return PASSWORD_RESET;
    }

    @Override
    protected AbstractTokenServiceImpl tokenService() {
        return service;
    }

}
