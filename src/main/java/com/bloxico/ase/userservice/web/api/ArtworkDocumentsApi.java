package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.artwork.*;
import io.swagger.annotations.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "artworkDocuments")
public interface ArtworkDocumentsApi {

    // @formatter:off
    String ARTWORK_DOCUMENT_DOWNLOAD   = "/artwork/document/download";
    String ARTWORK_DOCUMENT_UPLOAD     = "/artwork/document/upload";
    String MNG_ARTWORK_DOCUMENT_UPLOAD = "/management/artwork/document/upload";
    String ARTWORK_DOCUMENT_DELETE     = "/artwork/document/delete";
    String MNG_ARTWORK_DOCUMENT_DELETE = "/management/artwork/document/delete";
    // @formatter:on

    @GetMapping(
            value = ARTWORK_DOCUMENT_DOWNLOAD,
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'download_artwork_document')")
    @ApiOperation(value = "Downloads artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork document successfully downloaded."),
            @ApiResponse(code = 404, message = "Document not found.")
    })
    ResponseEntity<Resource> downloadArtworkDocument(@Valid ArtworkDocumentRequest request);

    @PostMapping(
            value = ARTWORK_DOCUMENT_UPLOAD,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_artwork_document')")
    @ApiOperation(value = "Uploads artwork documents.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork documents successfully submitted."),
            @ApiResponse(code = 400, message = "Document(s) are not valid."),
            @ApiResponse(code = 401, message = "User not authorized to edit desired artwork."),
            @ApiResponse(code = 404, message = "Artwork not found."),
            @ApiResponse(code = 409, message = "Artwork already has document of category.")
    })
    ResponseEntity<UploadArtworkDocumentsResponse> uploadArtworkDocuments(UploadArtworkDocumentsRequest request);

    @PostMapping(
            value = MNG_ARTWORK_DOCUMENT_UPLOAD,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'upload_artwork_document_management')")
    @ApiOperation(value = "Uploads artwork documents.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork documents successfully submitted."),
            @ApiResponse(code = 400, message = "Document(s) are not valid."),
            @ApiResponse(code = 401, message = "User not authorized to edit desired artwork."),
            @ApiResponse(code = 404, message = "Artwork not found."),
            @ApiResponse(code = 409, message = "Artwork already has document of category.")
    })
    ResponseEntity<UploadArtworkDocumentsResponse> uploadArtworkDocumentsMng(UploadArtworkDocumentsRequest request);

    @DeleteMapping(
            value = ARTWORK_DOCUMENT_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_artwork_document')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork document successfully deleted."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork document."),
            @ApiResponse(code = 404, message = "Document not found.")
    })
    ResponseEntity<Void> deleteArtworkDocument(@Valid ArtworkDocumentRequest request);

    @DeleteMapping(
            value = MNG_ARTWORK_DOCUMENT_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_artwork_document_management')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork document successfully deleted."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork document."),
            @ApiResponse(code = 404, message = "Document not found.")
    })
    ResponseEntity<Void> deleteArtworkDocumentMng(@Valid ArtworkDocumentRequest request);

}
