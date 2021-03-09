package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.web.api.ArtworkMetadataApi;
import com.bloxico.ase.userservice.web.model.artwork.metadata.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtworkMetadataController implements ArtworkMetadataApi {

    @Autowired
    private IArtworkMetadataFacade artworkMetadataFacade;

    @Override
    public ResponseEntity<SaveArtworkMetadataResponse> saveArtworkMetadata(
            SaveArtworkMetadataRequest request)
    {
        var saved = artworkMetadataFacade.saveArtworkMetadata(request);
        var response = new SaveArtworkMetadataResponse(saved);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UpdateArtworkMetadataResponse> updateArtworkMetadata(
            UpdateArtworkMetadataRequest request)
    {
        var updated = artworkMetadataFacade.updateArtworkMetadata(request);
        var response = new UpdateArtworkMetadataResponse(updated);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteArtworkMetadata(String name, Type type) {
        artworkMetadataFacade.deleteArtworkMetadata(name, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> searchMetadata(
            Type type, Status status, String name, int page, int size, String sort)
    {
        var response = artworkMetadataFacade.searchArtworkMetadata(type, status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchArtworkMetadataResponse> searchApprovedArtworkMetadata(Type type, String name) {
        var response = artworkMetadataFacade.searchApprovedArtworkMetadata(name, type);
        return ResponseEntity.ok(response);
    }

}
