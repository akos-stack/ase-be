package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "artworkMetadata")
public interface ArtworkMetadataApi {

    // @formatter:off
    String ARTWORK_METADATA_SAVE            = "/management/artwork/metadata/save";
    String ARTWORK_METADATA_UPDATE          = "/management/artwork/metadata/update";
    String ARTWORK_METADATA_DELETE          = "/management/artwork/metadata/delete";
    String ARTWORK_METADATA_SEARCH          = "/management/artwork/metadata/search";
    String ARTWORK_METADATA_SEARCH_APPROVED = "/artwork/metadata/search";
    // @formatter:on

    @PostMapping(
            value = ARTWORK_METADATA_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Saves artwork metadata in database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully saved.")
    })
    ResponseEntity<SaveArtworkMetadataResponse> saveArtworkMetadata(
            @Valid @RequestBody SaveArtworkMetadataRequest request);

    @PostMapping(
            value = ARTWORK_METADATA_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Updates artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully updated."),
            @ApiResponse(code = 404, message = "Artwork metadata not found.")
    })
    ResponseEntity<UpdateArtworkMetadataResponse> updateArtworkMetadata(
            @Valid @RequestBody UpdateArtworkMetadataRequest request);

    @DeleteMapping(
            value = ARTWORK_METADATA_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Deletes artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully deleted."),
            @ApiResponse(code = 404, message = "Artwork metadata not found.")
    })
    ResponseEntity<Void> deleteArtworkMetadata(@Valid DeleteArtworkMetadataRequest request);

    @GetMapping(value = ARTWORK_METADATA_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully searched.")
    })
    ResponseEntity<SearchArtworkMetadataResponse> searchArtworkMetadata(
            @Valid SearchArtworkMetadataRequest request,
            @Valid PageRequest page);

    @GetMapping(value = ARTWORK_METADATA_SEARCH_APPROVED)
    @ApiOperation(value = "Searches approved artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Approved artwork metadata successfully searched.")
    })
    ResponseEntity<SearchApprovedArtworkMetadataResponse> searchApprovedArtworkMetadata(
            @Valid SearchApprovedArtworkMetadataRequest request);

}
