package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;

@Api(value = "artwork")
public interface ArtworkDocumentsApi {

    String SAVE_ARTWORK_DOCUMENTS   = "/artwork/documents";

    @PostMapping(
            value = SAVE_ARTWORK_DOCUMENTS,
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
}
