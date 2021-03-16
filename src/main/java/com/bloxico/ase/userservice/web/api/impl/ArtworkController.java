package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContext;
import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.web.api.ArtworkApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtworkController implements ArtworkApi {

    @Autowired private IArtworkFacade artworkFacade;
    @Autowired private AseSecurityContext security;

    @Override
    public ResponseEntity<ArtworkResponse> createArtworkDraft() {
        var response = artworkFacade.createArtworkDraft();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DetailedArtworkResponse> findArtworkById(FindByArtworkIdRequest request) {
        var response = artworkFacade.findArtworkById(WithOwner.of(security.getArtOwnerId(), request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DetailedArtworkResponse> findArtworkByIdMng(FindByArtworkIdRequest request) {
        var response = artworkFacade.findArtworkById(WithOwner.any(request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DetailedArtworkResponse> updateArtworkData(UpdateArtworkDataRequest request) {
        var response = artworkFacade.updateArtworkData(WithOwner.of(security.getArtOwnerId(), request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DetailedArtworkResponse> updateArtworkDataMng(UpdateArtworkDataRequest request) {
        var response = artworkFacade.updateArtworkData(WithOwner.any(request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchArtworkResponse> searchArtworks(SearchArtworkRequest request, PageRequest page) {
        var response = artworkFacade.searchArtworks(WithOwner.of(security.getArtOwnerId(), request), page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchArtworkResponse> searchArtworksMng(SearchArtworkRequest request, PageRequest page) {
        var response = artworkFacade.searchArtworks(WithOwner.any(request), page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteArtwork(DeleteArtworkRequest request) {
        artworkFacade.deleteArtwork(WithOwner.of(security.getArtOwnerId(), request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteArtworkMng(DeleteArtworkRequest request) {
        artworkFacade.deleteArtwork(WithOwner.any(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
