package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Api(value = "system")
public interface SystemApi {

    String SYSTEM_CONSTANTS = "/system/constants";

    @GetMapping(
            value = SYSTEM_CONSTANTS,
            produces = {"application/json"})
    @ApiOperation(value = "Returns all constants of the system, i.e. permissions and all the enums.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All constants of the system.")
    })
    ResponseEntity<SystemConstantsResponse> systemConstants();

}
