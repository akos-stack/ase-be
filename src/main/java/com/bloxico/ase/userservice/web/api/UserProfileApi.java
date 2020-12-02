package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "userProfile")
public interface UserProfileApi {

    String MY_PROFILE_ENDPOINT        = "/user/my-profile";
    String MY_PROFILE_UPDATE_ENDPOINT = "/user/my-profile/update";
    String USER_DISABLE               = "/user/disable";
    String USER_BLACKLIST_TOKENS      = "/user/blacklist-tokens";

    @GetMapping(value = MY_PROFILE_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'access_profile')")
    @ApiOperation(value = "Fetches profile data of the requester.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile data successfully retrieved.")
    })
    ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal);

    @PostMapping(
            value = MY_PROFILE_UPDATE_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_profile')")
    @ApiOperation(value = "Updates profile data of the requester.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile data successfully updated.")
    })
    ResponseEntity<UserProfileDataResponse> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request, Principal principal);

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
