package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationRequest;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationResponse;
import com.bloxico.ase.userservice.web.model.auth.BlacklistRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface AuthenticationApi {

    String AUTHENTICATE_ENDPOINT = "/authenticate",
           BLACKLIST_ENDPOINT    = "/ase/jwt/blacklist";

    @PostMapping(
            value = AUTHENTICATE_ENDPOINT,
            consumes = {"application/json"},
            produces = {"application/json"})
    ResponseEntity<AuthenticationResponse>
        authenticate
            (@RequestBody @Valid
             AuthenticationRequest body);

    @PostMapping(
            value = BLACKLIST_ENDPOINT,
            consumes = {"application/json"},
            produces = {"application/json"})
    ResponseEntity<Void>
        blacklist
            (@RequestAttribute("decodedJwt")
             DecodedJwtDto decodedJwt,
             @RequestBody @Valid
             BlacklistRequest request);

}
