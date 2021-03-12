package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Api(value = "artworkDocuments")
public interface ArtworkDocumentsApi {

    String ARTWORK_SAVE_DOCUMENTS     = "/artwork/documents";
    String ARTWORK_DOCUMENTS_DOWNLOAD = "/artwork/documents/download";
    String ARTWORK_DOCUMENTS_REMOVE   = "/artwork/documents/remove";

    @PostMapping(
            value = ARTWORK_SAVE_DOCUMENTS,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits artwork documents.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted artwork documents."),
            @ApiResponse(code = 400, message = "Documents are not valid."),
            @ApiResponse(code = 401, message = "User not authorized to edit desired artwork."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Artwork not found."),
            @ApiResponse(code = 409, message = "Artwork already has document of category.")
    })
    ResponseEntity<SaveArtworkResponse> saveArtworkDocuments(SaveArtworkDocumentsRequest request);

    @GetMapping(
            value = ARTWORK_DOCUMENTS_DOWNLOAD,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'preview_artwork')")
    @ApiOperation(value = "User downloads file.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully fetched artwork document."),
            @ApiResponse(code = 401, message = "User not authorized to fetch desired artwork document."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Document not found.")
    })
    ResponseEntity<Resource> downloadArtworkDocument(@Valid ArtworkDocumentRequest request);

    @DeleteMapping(
            value = ARTWORK_DOCUMENTS_REMOVE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'remove_artwork_document')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted artwork document."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork document."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Document not found.")
    })
    ResponseEntity<SaveArtworkResponse> deleteArtworkDocument(@Valid ArtworkDocumentRequest request);
}
