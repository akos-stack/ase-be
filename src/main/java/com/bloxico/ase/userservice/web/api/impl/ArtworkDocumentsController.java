package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContextService;
import com.bloxico.ase.userservice.facade.IArtworkDocumentsFacade;
import com.bloxico.ase.userservice.web.api.ArtworkDocumentsApi;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtworkDocumentsController implements ArtworkDocumentsApi {

    @Autowired private IArtworkDocumentsFacade artworkDocumentsFacade;
    @Autowired private AseSecurityContextService security;

    @Override
    public ResponseEntity<Resource> downloadArtworkDocument(ArtworkDocumentRequest request) {
        var response = artworkDocumentsFacade.downloadArtworkDocument(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UploadArtworkDocumentsResponse> uploadArtworkDocuments(UploadArtworkDocumentsRequest request) {
        var response = artworkDocumentsFacade.uploadArtworkDocuments(WithOwner.any(request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UploadArtworkDocumentsResponse> uploadArtworkDocumentsMng(UploadArtworkDocumentsRequest request) {
        var response = artworkDocumentsFacade.uploadArtworkDocuments(WithOwner.of(security.getArtOwnerId(), request));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteArtworkDocument(ArtworkDocumentRequest request) {
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.any(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteArtworkDocumentMng(ArtworkDocumentRequest request) {
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.of(security.getArtOwnerId(), request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
