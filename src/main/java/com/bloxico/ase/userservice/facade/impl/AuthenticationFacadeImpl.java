package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IAuthenticationFacade;
import com.bloxico.ase.userservice.service.token.IJwtService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationRequest;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationFacadeImpl implements IAuthenticationFacade {

    private final IJwtService jwtService;
    private final IUserProfileService userProfileService;

    @Autowired
    public AuthenticationFacadeImpl(IJwtService jwtService,
                                    IUserProfileService userProfileService)
    {
        this.jwtService = jwtService;
        this.userProfileService = userProfileService;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.debug("AuthenticationFacadeImpl.authenticate - start | request#email: {}", request.getEmail());
        var userProfile = userProfileService.findUserProfileByEmail(request.getEmail());
        userProfileService.checkPassword(request.getPassword(), userProfile.getPassword());
        var token = jwtService.generateToken(userProfile);
        log.debug("AuthenticationFacadeImpl.authenticate - end | request#email: {}", request.getEmail());
        return new AuthenticationResponse(token, userProfile);
    }

    @Override
    public void blacklist(long principalId, String token) {
        log.debug("AuthenticationFacadeImpl.blacklist - start | principalId: {}, token: {}", principalId, token);
        jwtService.blacklistToken(principalId, token);
        log.debug("AuthenticationFacadeImpl.blacklist - end | principalId: {}, token: {}", principalId, token);
    }

}
