package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.entity.config.Config.Type;
import com.bloxico.ase.userservice.web.model.config.*;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "config")
public interface ConfigApi {

    // @formatter:off
    String CONFIG_SEARCH = "/config";
    String CONFIG_SAVE   = "/config/save";
    // @formatter:on

    @GetMapping(
            value = CONFIG_SEARCH,
            produces = {"application/json"})
    @ApiOperation(value = "Search config by type.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Config of specified type successfully found."),
            @ApiResponse(code = 404, message = "Config of specified type not found.")
    })
    ResponseEntity<SearchConfigResponse> searchConfig(@Valid @RequestParam("type") Type type);

    @PostMapping(
            value = CONFIG_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_config')")
    @ApiOperation(value = "Saves config in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Config successfully saved.")
    })
    ResponseEntity<SaveConfigResponse> saveConfig(@Valid @RequestBody SaveConfigRequest request);

}
