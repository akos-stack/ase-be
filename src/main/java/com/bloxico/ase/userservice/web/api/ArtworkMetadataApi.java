package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkMetadataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Api(value = "artworksMetadata")
public interface ArtworkMetadataApi {

    String SEARCH_ARTWORK_METADATA = "/artwork/metadata";

    @GetMapping(value = SEARCH_ARTWORK_METADATA)
    @ApiOperation(value = "User fetches approved metadata")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Metadata successfully fetched.")
    })
    ResponseEntity<SearchArtworkMetadataResponse> searchApprovedArtworkMetadata(@Valid @RequestParam(value = "type") ArtworkMetadataType type, @Valid @RequestParam(value = "name", required = false) String name);
}
