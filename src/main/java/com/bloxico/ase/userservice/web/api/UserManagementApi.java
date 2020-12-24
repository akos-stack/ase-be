package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
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
import java.security.Principal;

@Api(value = "userManagement")
public interface UserManagementApi {

    String USER_MANAGEMENT_ENDPOINT        = "/users";
    String USER_DISABLE                    = "/users/disable";
    String USER_BLACKLIST_TOKENS           = "/users/blacklist-tokens";

    @GetMapping(value = USER_MANAGEMENT_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'search_users')")
    @ApiOperation(value = "Search users by email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paginated list of users successfully retrieved.")
    })
    ResponseEntity<ArrayUserProfileDataResponse> searchUsers(@Valid @RequestParam("email") String email, @Valid @RequestParam(required = false, defaultValue = "0") int page, @Valid @RequestParam(required = false, defaultValue = "10") @Min(1) int size, @Valid @RequestParam(required = false, defaultValue = "name") String sort);

    @PostMapping(
            value = USER_DISABLE,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'disable_user')")
    @ApiOperation(value = "Disables (blocks) a user and blacklists all its tokens. " +
            "User won't be able to authorize with previous tokens. " +
            "User won't be able to authenticate unless enabled.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully disabled."),
            @ApiResponse(code = 404, message = "Could not find user associated with the given id.")
    })
    ResponseEntity<Void> disableUser(@Valid @RequestBody DisableUserRequest request, Principal principal);

    @PostMapping(
            value = USER_BLACKLIST_TOKENS,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'blacklist_token')")
    @ApiOperation(value = "Blacklists all tokens issued for the given user. " +
            "User won't be able to authorize with previous tokens. " +
            "User will be able to authenticate, i.e. to obtain a new token, if not disabled.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tokens successfully blacklisted."),
            @ApiResponse(code = 404, message = "Could not find user associated with the given id.")
    })
    ResponseEntity<Void> blacklistTokens(@Valid @RequestBody BlacklistTokensRequest request, Principal principal);
}
