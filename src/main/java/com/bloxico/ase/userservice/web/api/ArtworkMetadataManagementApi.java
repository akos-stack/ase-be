package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;

@Api(value = "artworksMetadataManagement")
public interface ArtworkMetadataManagementApi {

    String CREATE_METADATA        = "/artwork/management/metadata/create";
    String UPDATE_METADATA_STATUS = "/artwork/management/metadata/update";
    String DELETE_METADATA        = "/artwork/management/metadata/delete";
    String SEARCH_METADATA        = "/artwork/management/metadata";

    @PostMapping(
            value = CREATE_METADATA,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User creates artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted metadata.")
    })
    ResponseEntity<Void> createMetadata(@Valid @RequestBody ArtworkMetadataCreateRequest request, Principal principal);

    @PostMapping(
            value = UPDATE_METADATA_STATUS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User updates metadata status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully updated metadata."),
            @ApiResponse(code = 404, message = "Category not found.")
    })
    ResponseEntity<Void> updateMetadataStatus(@Valid @RequestBody ArtworkMetadataUpdateRequest request, Principal principal);

    @DeleteMapping(
            value = DELETE_METADATA,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User deletes metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted metadata."),
            @ApiResponse(code = 404, message = "Metadata not found.")
    })
    ResponseEntity<Void> deleteMetadata(@RequestParam(value = "name") String name, @RequestParam(value = "type") ArtworkMetadataType type);

    @GetMapping(value = SEARCH_METADATA)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User fetches all categories")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Categories successfully fetched.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> searchMetadata(
            @Valid @RequestParam(value = "type") ArtworkMetadataType type,
            @Valid @RequestParam(value = "status", required = false) ArtworkMetadataStatus status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);
}
