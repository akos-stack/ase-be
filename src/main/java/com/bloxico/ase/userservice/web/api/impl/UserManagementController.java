package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.web.api.UserManagementApi;
import com.bloxico.ase.userservice.web.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.bloxico.ase.userservice.util.Principals.extractId;

@RestController
public class UserManagementController implements UserManagementApi {

    @Autowired
    private IUserManagementFacade userManagementFacade;

    @Override
    public ResponseEntity<PagedUserDataResponse> searchUsers(String email, String role, int page, int size, String sort) {
        var response = userManagementFacade.searchUsers(email, role, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> disableUser(DisableUserRequest request, Principal principal) {
        var id = extractId(principal);
        userManagementFacade.disableUser(request.getUserId(), id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> blacklistTokens(BlacklistTokensRequest request, Principal principal) {
        var id = extractId(principal);
        userManagementFacade.blacklistTokens(request.getUserId(), id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
