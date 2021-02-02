package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.web.api.ArtworkMetadataApi;
import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtworkMetadataController implements ArtworkMetadataApi {

    @Autowired
    private IArtworkMetadataFacade artworkMetadataFacade;

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchCategories() {
        var response = artworkMetadataFacade.fetchApprovedCategories();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchMaterials() {
        var response = artworkMetadataFacade.fetchApprovedMaterials();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchMediums() {
        var response = artworkMetadataFacade.fetchApprovedMediums();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchStyles() {
        var response = artworkMetadataFacade.fetchApprovedStyles();
        return ResponseEntity.ok(response);
    }
}
