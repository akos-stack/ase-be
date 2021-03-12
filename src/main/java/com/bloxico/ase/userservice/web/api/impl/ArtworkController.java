package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.web.api.ArtworkApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public ResponseEntity<SearchArtworkResponse> searchArtworks(@Valid SearchArtworkRequest request, @Valid PageRequest page) {
        var response = artworkFacade.searchArtworks(request, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteArtwork(@Valid Long artworkId) {
        artworkFacade.deleteArtwork(artworkId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
