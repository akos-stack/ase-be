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
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchCategories(String name) {
        var response = artworkMetadataFacade.fetchApprovedCategories(name);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchMaterials(String name) {
        var response = artworkMetadataFacade.fetchApprovedMaterials(name);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchMediums(String name) {
        var response = artworkMetadataFacade.fetchApprovedMediums(name);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArrayArtworkMetadataResponse> fetchStyles(String name) {
        var response = artworkMetadataFacade.fetchApprovedStyles(name);
        return ResponseEntity.ok(response);
    }
}
