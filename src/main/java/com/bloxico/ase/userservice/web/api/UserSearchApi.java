package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "userSearch")
public interface UserSearchApi {

    String USER_SEARCH_ENDPOINT        = "/user/search";

    @GetMapping(value = USER_SEARCH_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'access_profile')")
    @ApiOperation(value = "Search users by email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of users successfully retrieved.")
    })
    ResponseEntity<ArrayUserProfileDataResponse> searchUsers(@Valid @RequestParam("email") String email, Principal principal);
}
