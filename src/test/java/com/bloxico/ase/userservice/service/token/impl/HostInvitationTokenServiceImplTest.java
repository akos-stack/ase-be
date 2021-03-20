package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.Token;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.token.Token.Type.HOST_INVITATION;

public class HostInvitationTokenServiceImplTest extends AbstractTokenServiceImplTest {

    @Autowired
    private HostInvitationTokenServiceImpl service;

    @Override
    protected Token.Type tokenType() {
        return HOST_INVITATION;
    }

    @Override
    protected AbstractTokenServiceImpl tokenService() {
        return service;
    }

}
