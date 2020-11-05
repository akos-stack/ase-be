package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.dto.token.DecodedJwtDto;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationRequest;
import com.bloxico.ase.userservice.web.model.auth.AuthenticationResponse;
import com.bloxico.ase.userservice.web.model.auth.BlacklistRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(value = "authentication")
public interface AuthenticationApi {


    String AUTHENTICATE_ENDPOINT = "/authenticate",
           BLACKLIST_ENDPOINT    = "/ase/jwt/blacklist";


    @ApiOperation(value = "Creates a new JWT for user.")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "JWT is created successfully."),
            @ApiResponse(
                    code = 404,
                    message = "User is not found.")})
    @PostMapping(
            value = AUTHENTICATE_ENDPOINT,
            consumes = {"application/json"},
            produces = {"application/json"})
    ResponseEntity<AuthenticationResponse>
        authenticate
            (@RequestBody @Valid
             AuthenticationRequest body);


    @ApiOperation(value = "Blacklists a JWT.")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "JWT is blacklisted successfully.")})
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
