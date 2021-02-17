package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Api(value = "artwork")
public interface ArtworkApi {

    String SUBMIT_ARTWORK = "/artwork";

    @PostMapping(
            value = SUBMIT_ARTWORK,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits artwork.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted artwork.")
    })
    ResponseEntity<SubmitArtworkResponse> submitArtwork(SubmitArtworkRequest request, Principal principal);
}
