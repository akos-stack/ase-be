package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.facade.IArtworkMetadataManagementFacade;
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
    private IArtworkMetadataManagementFacade artworkMetadataFacade;

    @Override
    public ResponseEntity<Void> createCategory(@Valid ArtworkMetadataCreateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.createCategory(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateCategoryStatus(@Valid ArtworkMetadataUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.updateCategory(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(String name) {
        artworkMetadataFacade.deleteCategory(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> fetchCategories(@Valid ArtworkMetadataStatus status, @Valid String name, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkMetadataFacade.fetchCategories(status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createMaterial(@Valid ArtworkMetadataCreateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.createMaterial(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateMaterialStatus(@Valid ArtworkMetadataUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.updateMaterial(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteMaterial(String name) {
        artworkMetadataFacade.deleteMaterial(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> fetchMaterials(@Valid ArtworkMetadataStatus status, @Valid String name, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkMetadataFacade.fetchMaterials(status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createMedium(@Valid ArtworkMetadataCreateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.createMedium(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateMediumStatus(@Valid ArtworkMetadataUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.updateMedium(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteMedium(String name) {
        artworkMetadataFacade.deleteMedium(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> fetchMediums(@Valid ArtworkMetadataStatus status, @Valid String name, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkMetadataFacade.fetchMediums(status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createStyle(@Valid ArtworkMetadataCreateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.createStyle(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateStyleStatus(@Valid ArtworkMetadataUpdateRequest request, Principal principal) {
        var id = extractId(principal);
        artworkMetadataFacade.updateStyle(request, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteStyle(String name) {
        artworkMetadataFacade.deleteStyle(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedArtworkMetadataResponse> fetchStyles(@Valid ArtworkMetadataStatus status, @Valid String name, @Valid int page, @Valid @Min(1) int size, @Valid String sort) {
        var response = artworkMetadataFacade.fetchStyles(status, name, page, size, sort);
        return ResponseEntity.ok(response);
    }
}
