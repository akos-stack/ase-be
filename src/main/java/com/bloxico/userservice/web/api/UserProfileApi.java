package com.bloxico.userservice.web.api;

import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.userservice.web.api.Temp.OLD;

//@Api(value = "userProfile", description = "User profile API")
public interface UserProfileApi {

    String MY_PROFILE_ENDPOINT = OLD + "/user/myProfile";
    String UPDATE_MY_PROFILE_ENDPOINT = OLD + "/user/updateMyProfile";
    String INSERT_WALLET_ADDRESS = OLD + "/user/insertWalletAddress";
    String DELETE_WALLET_ADDRESS = OLD + "/user/deleteWalletAddress";

    @GetMapping(value = MY_PROFILE_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'access_profile')")
//    @ApiOperation(value = "Endpoint used to fetch user profile data.")
//    @ApiResponses({@ApiResponse(code = 200, message = "Profile data successfully retrieved.")
//    })
    ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal);

    @PostMapping(value = UPDATE_MY_PROFILE_ENDPOINT)
    @PreAuthorize("@permissionSecurity.isAuthorized(authentication, 'update_profile')")
//    @ApiOperation(value = "Endpoint used to update user profile.")
//    @ApiResponses({@ApiResponse(code = 200, message = "Update successful.")
//    })
    ResponseEntity<UserProfileDataResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest, Principal principal);
}
