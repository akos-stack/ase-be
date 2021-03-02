package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.web.api.ArtworkApi;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class ArtworkController implements ArtworkApi {

    @Autowired
    private IArtworkFacade artworkFacade;

    @Override
    public ResponseEntity<SaveArtworkResponse> submitArtwork(SaveArtworkRequest request, Principal principal) {
        var id = extractId(principal);
        var response = artworkFacade.submitArtwork(request, id);
        return ResponseEntity.ok(response);
    }
}