package com.bloxico.userservice.web.api;

import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "userProfile", description = "User profile API")
public interface UserProfileApi {

    String MY_PROFILE_ENDPOINT = "/user/myProfile";
    String UPDATE_MY_PROFILE_ENDPOINT = "/user/updateMyProfile";
    String INSERT_WALLET_ADDRESS = "/user/insertWalletAddress";
    String DELETE_WALLET_ADDRESS = "/user/deleteWalletAddress";

    @GetMapping(value = MY_PROFILE_ENDPOINT)
    @ApiOperation(value = "Endpoint used to fetch user profile data.")
    @ApiResponses({@ApiResponse(code = 200, message = "Profile data successfully retrieved.")
    })
    ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal);

    @PostMapping(value = UPDATE_MY_PROFILE_ENDPOINT)
    @ApiOperation(value = "Endpoint used to update user profile.")
    @ApiResponses({@ApiResponse(code = 200, message = "Update successful.")
    })
    ResponseEntity<Void> updateMyProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest, Principal principal);
}
