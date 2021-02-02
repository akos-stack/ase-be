package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Api(value = "artworksMetadata")
public interface ArtworkMetadataApi {

    String FETCH_CATEGORIES       = "/artwork/categories";
    String FETCH_MATERIALS        = "/artwork/materials";
    String FETCH_MEDIUMS          = "/artwork/mediums";
    String FETCH_STYLES           = "/artwork/styles";

    @GetMapping(value = FETCH_CATEGORIES)
    @ApiOperation(value = "User fetches approved categories")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Categories successfully fetched.")
    })
    ResponseEntity<ArrayArtworkMetadataResponse> fetchCategories();

    @GetMapping(value = FETCH_MATERIALS)
    @ApiOperation(value = "User fetches approved materials")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Materials successfully fetched.")
    })
    ResponseEntity<ArrayArtworkMetadataResponse> fetchMaterials();

    @GetMapping(value = FETCH_MEDIUMS)
    @ApiOperation(value = "User fetches approved mediums")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mediums successfully fetched.")
    })
    ResponseEntity<ArrayArtworkMetadataResponse> fetchMediums();

    @GetMapping(value = FETCH_STYLES)
    @ApiOperation(value = "User fetches approved styles")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Styles successfully fetched."),
            @ApiResponse(code = 404, message = "Status not found.")
    })
    ResponseEntity<ArrayArtworkMetadataResponse> fetchStyles();
}
