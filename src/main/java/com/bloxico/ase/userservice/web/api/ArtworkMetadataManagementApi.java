package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
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

    String CREATE_CATEGORY        = "/artwork/management/category";
    String UPDATE_CATEGORY_STATUS = "/artwork/management/category/update";
    String DELETE_CATEGORY        = "/artwork/management/category/delete";
    String FETCH_CATEGORIES       = "/artwork/management/categories";
    String CREATE_MATERIAL        = "/artwork/management/material";
    String UPDATE_MATERIAL_STATUS = "/artwork/management/material/update";
    String DELETE_MATERIAL        = "/artwork/management/material/delete";
    String FETCH_MATERIALS        = "/artwork/management/materials";
    String CREATE_MEDIUM          = "/artwork/management/medium";
    String UPDATE_MEDIUM_STATUS   = "/artwork/management/medium/update";
    String DELETE_MEDIUM          = "/artwork/management/medium/delete";
    String FETCH_MEDIUMS          = "/artwork/management/mediums";
    String CREATE_STYLE           = "/artwork/management/style";
    String UPDATE_STYLE_STATUS    = "/artwork/management/style/update";
    String DELETE_STYLE           = "/artwork/management/style/delete";
    String FETCH_STYLES           = "/artwork/management/styles";

    @PostMapping(
            value = CREATE_CATEGORY,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User creates artwork category.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted category.")
    })
    ResponseEntity<Void> createCategory(@Valid @RequestBody ArtworkMetadataCreateRequest request, Principal principal);

    @PostMapping(
            value = UPDATE_CATEGORY_STATUS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User updates category status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully updated category."),
            @ApiResponse(code = 404, message = "Category not found.")
    })
    ResponseEntity<Void> updateCategoryStatus(@Valid @RequestBody ArtworkMetadataUpdateRequest request, Principal principal);

    @DeleteMapping(
            value = DELETE_CATEGORY,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User deletes category.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted category."),
            @ApiResponse(code = 404, message = "Category not found.")
    })
    ResponseEntity<Void> deleteCategory(@RequestParam(value = "name") String name);

    @GetMapping(value = FETCH_CATEGORIES)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User fetches all categories")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Categories successfully fetched.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> fetchCategories(
            @Valid @RequestParam(value = "status", required = false) ArtworkMetadataStatus status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);

    @PostMapping(
            value = CREATE_MATERIAL,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User creates artwork material.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted material.")
    })
    ResponseEntity<Void> createMaterial(@Valid @RequestBody ArtworkMetadataCreateRequest request, Principal principal);

    @PostMapping(
            value = UPDATE_MATERIAL_STATUS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User updates material status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully updated material."),
            @ApiResponse(code = 404, message = "Material not found.")
    })
    ResponseEntity<Void> updateMaterialStatus(@Valid @RequestBody ArtworkMetadataUpdateRequest request, Principal principal);

    @DeleteMapping(
            value = DELETE_MATERIAL,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User deletes material.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted material."),
            @ApiResponse(code = 404, message = "Material not found.")
    })
    ResponseEntity<Void> deleteMaterial(@RequestParam(value = "name") String name);

    @GetMapping(value = FETCH_MATERIALS)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User fetches all materials")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Materials successfully fetched.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> fetchMaterials(
            @Valid @RequestParam(value = "status", required = false) ArtworkMetadataStatus status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);

    @PostMapping(
            value = CREATE_MEDIUM,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User creates artwork medium.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted medium.")
    })
    ResponseEntity<Void> createMedium(@Valid @RequestBody ArtworkMetadataCreateRequest request, Principal principal);

    @PostMapping(
            value = UPDATE_MEDIUM_STATUS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User updates medium status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully updated medium."),
            @ApiResponse(code = 404, message = "Medium not found.")
    })
    ResponseEntity<Void> updateMediumStatus(@Valid @RequestBody ArtworkMetadataUpdateRequest request, Principal principal);

    @DeleteMapping(
            value = DELETE_MEDIUM,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User deletes medium.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted medium."),
            @ApiResponse(code = 404, message = "Medium not found.")
    })
    ResponseEntity<Void> deleteMedium(@RequestParam(value = "name") String name);

    @GetMapping(value = FETCH_MEDIUMS)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User fetches all mediums")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Mediums successfully fetched.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> fetchMediums(
            @Valid @RequestParam(value = "status", required = false) ArtworkMetadataStatus status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);

    @PostMapping(
            value = CREATE_STYLE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User creates artwork style.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully submitted style.")
    })
    ResponseEntity<Void> createStyle(@Valid @RequestBody ArtworkMetadataCreateRequest request, Principal principal);

    @PostMapping(
            value = UPDATE_STYLE_STATUS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User updates style status.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully updated style."),
            @ApiResponse(code = 404, message = "Style not found.")
    })
    ResponseEntity<Void> updateStyleStatus(@Valid @RequestBody ArtworkMetadataUpdateRequest request, Principal principal);

    @DeleteMapping(
            value = DELETE_STYLE,
            produces = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User deletes style.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted style."),
            @ApiResponse(code = 404, message = "Style not found.")
    })
    ResponseEntity<Void> deleteStyle(@RequestParam(value = "name") String name);

    @GetMapping(value = FETCH_STYLES)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'manage_artwork_metadata')")
    @ApiOperation(value = "User fetches all styles")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Styles successfully fetched."),
            @ApiResponse(code = 404, message = "Status not found.")
    })
    ResponseEntity<PagedArtworkMetadataResponse> fetchStyles(
            @Valid @RequestParam(value = "status", required = false) ArtworkMetadataStatus status,
            @Valid @RequestParam(value = "name", required = false) String name,
            @Valid @RequestParam(required = false, defaultValue = "0") int page,
            @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(required = false, defaultValue = "name") String sort);
}
