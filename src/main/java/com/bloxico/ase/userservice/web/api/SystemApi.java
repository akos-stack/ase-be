package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.config.*;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "system")
public interface SystemApi {

    // @formatter:off
    String SYSTEM_CONSTANTS     = "/system/constants";
    String SYSTEM_CONFIG_SEARCH = "/system/config";
    String SYSTEM_CONFIG_SAVE   = "/system/config/save";
    // @formatter:on

    @GetMapping(
            value = SYSTEM_CONSTANTS,
            produces = {"application/json"})
    @ApiOperation(value = "Returns all constants of the system, i.e. permissions and all the enums.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All constants of the system.")
    })
    ResponseEntity<SystemConstantsResponse> systemConstants();

    @GetMapping(
            value = SYSTEM_CONFIG_SEARCH,
            produces = {"application/json"})
    @ApiOperation(value = "Search config by type.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Config of specified type successfully found."),
            @ApiResponse(code = 404, message = "Config of specified type not found.")
    })
    ResponseEntity<SearchConfigResponse> searchConfig(@Valid SearchConfigRequest request);

    @PostMapping(
            value = SYSTEM_CONFIG_SAVE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'save_config')")
    @ApiOperation(value = "Saves config in the database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Config successfully saved.")
    })
    ResponseEntity<SaveConfigResponse> saveConfig(@Valid @RequestBody SaveConfigRequest request);

}
