package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.web.api.ArtworkApi;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
public class ArtworkController implements ArtworkApi {

    @Autowired
    private IArtworkFacade artworkFacade;

    @Override
    public ResponseEntity<SaveArtworkResponse> previewArtwork(@Valid Long artworkId) {
        var response = artworkFacade.getArtworkById(artworkId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveArtworkResponse> saveArtworkDraft() {
        var response = artworkFacade.saveArtworkDraft();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveArtworkResponse> saveArtworkData(SaveArtworkDataRequest request) {
        var response = artworkFacade.saveArtworkData(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedArtworkResponse> searchArtworks(Artwork.@Valid Status status, @Valid String title, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkFacade.searchMyArtworks(status, title, page, size, sort);
        return ResponseEntity.ok(response);
    }
}
