package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Api(value = "artwork")
public interface ArtworkApi {

    String PREVIEW_ARTWORK    = "/artwork";
    String SAVE_ARTWORK_DRAFT = "/artwork/draft";
    String SAVE_ARTWORK_DATA  = "/artwork/data";
    String SEARCH_ARTWORKS    = "/artworks";

    @GetMapping(
            value = PREVIEW_ARTWORK,
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
            value = SAVE_ARTWORK_DRAFT,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits empty artwork in status DRAFT.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted draft artwork."),
            @ApiResponse(code = 403, message = "User has no permission.")
    })
    ResponseEntity<SaveArtworkResponse> saveArtworkDraft();

    @PostMapping(
            value = SAVE_ARTWORK_DATA,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'submit_artwork')")
    @ApiOperation(value = "User submits artwork data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted artwork data.")
    })
    ResponseEntity<SaveArtworkResponse> saveArtworkData(@Valid @RequestBody SaveArtworkDataRequest request);

    @GetMapping(value = SEARCH_ARTWORKS)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_artwork')")
    @ApiOperation(value = "Searches artwork metadata.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Artworks successfully searched.")
    })
    ResponseEntity<PagedArtworkResponse> searchArtworks(
            @Valid @RequestParam(value = "status", required = false) Artwork.Status status,
            @Valid @RequestParam(value = "title", required = false) String title,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "title") String sort);
}
