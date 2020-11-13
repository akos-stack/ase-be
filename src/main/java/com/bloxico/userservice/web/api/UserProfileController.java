package com.bloxico.userservice.web.api;

import com.bloxico.userservice.facade.IUserProfileFacade;
import com.bloxico.userservice.web.model.userprofile.UpdateProfileRequest;
import com.bloxico.userservice.web.model.userprofile.UserProfileDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class UserProfileController implements UserProfileApi {
    private IUserProfileFacade userProfileFacade;

    @Autowired
    public UserProfileController(IUserProfileFacade userProfileFacade) {
        this.userProfileFacade = userProfileFacade;
    }

    @Override
    public ResponseEntity<UserProfileDataResponse> accessMyProfile(Principal principal) {
        String email = principal.getName();
        UserProfileDataResponse userProfileDataResponse = userProfileFacade.returnMyProfileData(email);

        return new ResponseEntity<>(userProfileDataResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserProfileDataResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest, Principal principal) {
        String email = principal.getName();

        UserProfileDataResponse dataResponse = userProfileFacade.updateMyProfile(email, updateProfileRequest);

        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }
}
