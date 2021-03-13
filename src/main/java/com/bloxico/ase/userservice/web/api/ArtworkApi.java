package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "artwork")
public interface ArtworkApi {

    String ARTWORK_PREVIEW    = "/artwork";
    String ARTWORK_SAVE_DRAFT = "/artwork/draft";
    String ARTWORK_SAVE_DATA  = "/artwork/data";
    String ARTWORK_SEARCH     = "/artwork/search";
    String ARTWORK_DELETE     = "/artwork/delete";

    @GetMapping(
            value = ARTWORK_PREVIEW,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'preview_artwork')")
    @ApiOperation(value = "User previews submitted artwork.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully fetched artwork."),
            @ApiResponse(code = 401, message = "User not authorized to fetch desired artwork."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<SaveArtworkResponse> previewArtwork(@Valid @RequestParam(value = "id") Long artworkId);

    @PostMapping(
            value = ARTWORK_SAVE_DRAFT,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits empty artwork in status DRAFT.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted draft artwork."),
            @ApiResponse(code = 403, message = "User has no permission.")
    })
    ResponseEntity<SaveArtworkResponse> saveArtworkDraft();

    @PostMapping(
            value = ARTWORK_SAVE_DATA,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits artwork data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted artwork data."),
            @ApiResponse(code = 400, message = "User submitted bad data."),
            @ApiResponse(code = 401, message = "User not authorized to update artwork."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<SaveArtworkResponse> saveArtworkData(@Valid @RequestBody SaveArtworkDataRequest request);

    @GetMapping(value = ARTWORK_SEARCH)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_artwork')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artworks successfully searched."),
            @ApiResponse(code = 403, message = "User has no permission.")
    })
    ResponseEntity<SearchArtworkResponse> searchArtworks(
            @Valid SearchArtworkRequest request,
            @Valid PageRequest page);

    @DeleteMapping(
            value = ARTWORK_DELETE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'delete_artwork')")
    @ApiOperation(value = "Deletes artwork document.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted artwork."),
            @ApiResponse(code = 401, message = "User not authorized to delete desired artwork."),
            @ApiResponse(code = 403, message = "User has no permission."),
            @ApiResponse(code = 404, message = "Artwork not found.")
    })
    ResponseEntity<Void> deleteArtwork(@Valid @RequestParam(value = "id") Long artworkId);

}
