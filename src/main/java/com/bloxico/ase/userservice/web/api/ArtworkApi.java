package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "artwork")
public interface ArtworkApi {

    // @formatter:off
    String ARTWORK_CREATE_DRAFT = "/artwork/create-draft";
    String ARTWORK_PREVIEW      = "/artwork/preview";
    String MNG_ARTWORK_PREVIEW  = "/management/artwork/preview";
    String ARTWORK_UPDATE       = "/artwork/update";
    String MNG_ARTWORK_UPDATE   = "/management/artwork/update";
    String ARTWORK_SEARCH       = "/artwork/search";
    String MNG_ARTWORK_SEARCH   = "/management/artwork/search";
    String ARTWORK_DELETE       = "/artwork/delete";
    String MNG_ARTWORK_DELETE   = "/management/artwork/delete";
    // @formatter:on

    @PostMapping(
            value = ARTWORK_CREATE_DRAFT,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'create_artwork_draft')")
    @ApiOperation(value = "User creates an empty artwork with status 'DRAFT'.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Draft successfully created."),
            @ApiResponse(code = 403, message = "User has no permission.")
    })
    ResponseEntity<ArtworkResponse> createArtworkDraft();

    @GetMapping(
            value = ARTWORK_PREVIEW,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'preview_artwork')")
    @ApiOperation(value = "Fetches submitted artwork.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork successfully fetched."),
            @ApiResponse(code = 401, message = "User not authorized to fetch artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<DetailedArtworkResponse> findArtworkById(@Valid FindByArtworkIdRequest request);

    @GetMapping(
            value = MNG_ARTWORK_PREVIEW,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'preview_artwork_management')")
    @ApiOperation(value = "Fetches submitted artwork.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artwork successfully fetched."),
            @ApiResponse(code = 401, message = "User not authorized to fetch artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<DetailedArtworkResponse> findArtworkByIdMng(@Valid FindByArtworkIdRequest request);

    @PostMapping(
            value = ARTWORK_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_artwork')")
    @ApiOperation(value = "User updates artwork's data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Data successfully updated."),
            @ApiResponse(code = 400, message = "User submitted invalid data."),
            @ApiResponse(code = 401, message = "User not authorized to update artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<DetailedArtworkResponse> updateArtworkData(@Valid @RequestBody UpdateArtworkDataRequest request);

    @PostMapping(
            value = MNG_ARTWORK_UPDATE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_artwork_management')")
    @ApiOperation(value = "User updates artwork's data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Data successfully updated."),
            @ApiResponse(code = 400, message = "User submitted invalid data."),
            @ApiResponse(code = 401, message = "User not authorized to update artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<DetailedArtworkResponse> updateArtworkDataMng(@Valid @RequestBody UpdateArtworkDataRequest request);

    @GetMapping(value = ARTWORK_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_artwork')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artworks successfully searched.")
    })
    ResponseEntity<SearchArtworkResponse> searchArtworks(@Valid SearchArtworkRequest request, @Valid PageRequest page);

    @GetMapping(value = MNG_ARTWORK_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_artwork_management')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artworks successfully searched.")
    })
    ResponseEntity<SearchArtworkResponse> searchArtworksMng(@Valid SearchArtworkRequest request, @Valid PageRequest page);

    @DeleteMapping(
            value = ARTWORK_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_artwork')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted artwork."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<Void> deleteArtwork(@Valid DeleteArtworkRequest request);

    @DeleteMapping(
            value = MNG_ARTWORK_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_artwork_management')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted artwork."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<Void> deleteArtworkMng(@Valid DeleteArtworkRequest request);

}
