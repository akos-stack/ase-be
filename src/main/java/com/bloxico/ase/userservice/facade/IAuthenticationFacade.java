package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.auth.AuthenticationRequest;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationResponse;

public interface IAuthenticationFacade {

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void blacklist(long principalId, String token);

}
