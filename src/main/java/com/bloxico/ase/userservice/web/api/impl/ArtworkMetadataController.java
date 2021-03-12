package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.web.api.ArtworkMetadataApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
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
    public ResponseEntity<Void> deleteArtworkMetadata(DeleteArtworkMetadataRequest request) {
        artworkMetadataFacade.deleteArtworkMetadata(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SearchArtworkMetadataResponse> searchArtworkMetadata(
            SearchArtworkMetadataRequest request, PageRequest page)
    {
        var response = artworkMetadataFacade.searchArtworkMetadata(request, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchApprovedArtworkMetadataResponse> searchApprovedArtworkMetadata(
            SearchApprovedArtworkMetadataRequest request)
    {
        var response = artworkMetadataFacade.searchApprovedArtworkMetadata(request);
        return ResponseEntity.ok(response);
    }

}
