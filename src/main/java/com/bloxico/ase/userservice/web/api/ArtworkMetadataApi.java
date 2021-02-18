package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.web.model.artwork.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@Api(value = "artworksMetadataManagement")
public interface ArtworkMetadataApi {

    String ARTWORK_METADATA_SAVE     = "/artwork/management/metadata/save";
    String ARTWORK_METADATA_UPDATE   = "/artwork/management/metadata/update";
    String ARTWORK_METADATA_DELETE   = "/artwork/management/metadata/delete";
    String ARTWORK_METADATA_SEARCH   = "/artwork/management/metadata";
    String ARTWORK_METADATA_APPROVED = "/artwork/approved-metadata";

    @PostMapping(
            value = ARTWORK_METADATA_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Creates artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully submitted.")
    })
    ResponseEntity<SaveArtworkMetadataResponse> saveArtworkMetadata(
            @Valid @RequestBody SaveArtworkMetadataRequest request, Principal principal);

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
            @Valid @RequestBody UpdateArtworkMetadataRequest request, Principal principal);

    @DeleteMapping(
            value = ARTWORK_METADATA_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Deletes artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully deleted."),
            @ApiResponse(code = 404, message = "Artwork metadata not found.")
    })
    ResponseEntity<Void> deleteArtworkMetadata(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "type") Type type);

    @GetMapping(value = ARTWORK_METADATA_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully searched.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> searchMetadata(
            @Valid @RequestParam(value = "type") Type type,
            @Valid @RequestParam(value = "status", required = false) Status status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);

    @GetMapping(value = ARTWORK_METADATA_APPROVED)
    @ApiOperation(value = "Fetches approved artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork metadata successfully fetched.")
    })
    ResponseEntity<SearchArtworkMetadataResponse> searchApprovedArtworkMetadata(
            @Valid @RequestParam(value = "type") Type type,
            @Valid @RequestParam(value = "name", required = false) String name);

}
