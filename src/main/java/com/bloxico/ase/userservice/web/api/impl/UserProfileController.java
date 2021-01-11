package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserProfileFacade;
import com.bloxico.ase.userservice.web.api.UserProfileApi;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.UserProfileDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class UserProfileController implements UserProfileApi {

    @Autowired
    private IUserProfileFacade userProfileFacade;

    @Override
    public ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal) {
        var id = extractId(principal);
        var response = userProfileFacade.returnMyProfileData(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserProfileDataResponse> updateMyProfile(UpdateUserProfileRequest request, Principal principal) {
        var id = extractId(principal);
        var response = userProfileFacade.updateMyProfile(id, request);
        return ResponseEntity.ok(response);
    }

}
