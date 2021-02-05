package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.api.ArtworkMetadataManagementApi;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class ArtworkMetadataManagementController implements ArtworkMetadataManagementApi {

    @Autowired
    private IArtworkMetadataFacade artworkMetadataFacade;

    @Override
    public ResponseEntity<Void> createMetadata(@Valid ArtworkMetadataCreateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.createArtworkMetadata(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateMetadataStatus(@Valid ArtworkMetadataUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.updateArtworkMetadataStatus(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteMetadata(String name, ArtworkMetadataType type) {
        artworkMetadataFacade.deleteArtworkMetadata(name, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> searchMetadata(@Valid ArtworkMetadataType type, @Valid ArtworkMetadataStatus status, @Valid String name, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkMetadataFacade.searchArtworkMetadata(type, status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }
}
