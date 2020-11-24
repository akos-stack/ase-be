package com.bloxico.ase.userservice.web.api;

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

    String MY_PROFILE_ENDPOINT        = "/user/myProfile";
    String UPDATE_MY_PROFILE_ENDPOINT = "/user/updateMyProfile";

    @GetMapping(value = MY_PROFILE_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'access_profile')")
    @ApiOperation(value = "Endpoint used to fetch user profile data.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Profile data successfully retrieved.")
    })
    ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal);

    @PostMapping(
            value = UPDATE_MY_PROFILE_ENDPOINT,
            produces = {"application/json"},
            consumes = {"application/json"})
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_profile')")
    @ApiOperation(value = "Endpoint used to update user profile.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful.")
    })
    ResponseEntity<UserProfileDataResponse> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request, Principal principal);

}
