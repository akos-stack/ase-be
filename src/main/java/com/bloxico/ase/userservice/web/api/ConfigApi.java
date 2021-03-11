package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(value = "config")
public interface ConfigApi {

    String CONFIG_SAVE = "/config/save";

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
