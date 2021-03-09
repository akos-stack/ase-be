package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.web.api.UserManagementApi;
import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
import com.bloxico.ase.userservice.web.model.user.PagedUserDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> disableUser(DisableUserRequest request) {
        userManagementFacade.disableUser(request.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> blacklistTokens(BlacklistTokensRequest request) {
        userManagementFacade.blacklistTokens(request.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
