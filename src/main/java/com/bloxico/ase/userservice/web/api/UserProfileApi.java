package com.bloxico.ase.userservice.web.api;

import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "userProfile")
public interface UserProfileApi {

    String MY_PROFILE_ENDPOINT        = "/user/my-profile";
    String MY_PROFILE_UPDATE_ENDPOINT = "/user/my-profile/update";

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

}
