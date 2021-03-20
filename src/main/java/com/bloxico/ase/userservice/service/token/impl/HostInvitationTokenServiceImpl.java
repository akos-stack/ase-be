package com.bloxico.ase.userservice.service.token.impl;

import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostInvitationTokenServiceImpl extends AbstractTokenServiceImpl{

    @Autowired
    public HostInvitationTokenServiceImpl(TokenRepository tokenRepository) {
        super(tokenRepository);
    }

    @Override
    protected Token.Type getType() {
        return Token.Type.HOST_INVITATION;
    }

}
