package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.Token;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;

public class RegistrationTokenServiceImplTest extends AbstractTokenServiceImplTest {

    @Autowired
    private RegistrationTokenServiceImpl service;

    @Override
    protected Token.Type tokenType() {
        return REGISTRATION;
    }

    @Override
    protected AbstractTokenServiceImpl tokenService() {
        return service;
    }

}