package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import com.bloxico.ase.userservice.facade.IAuthenticationFacade;
import com.bloxico.ase.userservice.web.api.AuthenticationApi;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationRequest;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController implements AuthenticationApi {

    @Autowired
    private IAuthenticationFacade facade;

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest body) {
        return ResponseEntity.ok(facade.authenticate(body));
    }

    @Override
    public ResponseEntity<Void> blacklist(DecodedJwtDto decodedJwt, String token) {
        facade.blacklist(decodedJwt.getUserId(), token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
