package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserSearchFacade;
import com.bloxico.ase.userservice.web.api.UserSearchApi;
import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class UserSearchController implements UserSearchApi {

    @Autowired
    private IUserSearchFacade userSearchFacade;

    @Override
    public ResponseEntity<ArrayUserProfileDataResponse> searchUsers(@Valid String email, Principal principal) {
        var arrayUserProfileDataResponse = userSearchFacade.searchUsers(email);
        return ResponseEntity.ok(arrayUserProfileDataResponse);
    }
}
