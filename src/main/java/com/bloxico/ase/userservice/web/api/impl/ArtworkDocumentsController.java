package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IArtworkDocumentsFacade;
import com.bloxico.ase.userservice.web.api.ArtworkDocumentsApi;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ArtworkDocumentsController implements ArtworkDocumentsApi {

    @Autowired
    private IArtworkDocumentsFacade artworkDocumentsFacade;

    @Override
    public ResponseEntity<SaveArtworkResponse> saveArtworkDocuments(SaveArtworkDocumentsRequest request) {
        var response = artworkDocumentsFacade.saveArtworkDocuments(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Resource> downloadArtworkDocument(@Valid ArtworkDocumentRequest request) {
        var response = artworkDocumentsFacade.downloadArtworkDocument(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveArtworkResponse> deleteArtworkDocument(@Valid ArtworkDocumentRequest request) {
        var response = artworkDocumentsFacade.deleteArtworkDocument(request);
        return ResponseEntity.ok(response);
    }
}
