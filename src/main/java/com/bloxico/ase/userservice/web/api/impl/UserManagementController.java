package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.web.api.UserManagementApi;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserManagementController implements UserManagementApi {

    @Autowired
    private IUserManagementFacade userManagementFacade;

    @Override
    public ResponseEntity<SearchUsersResponse> searchUsers(SearchUsersRequest request, PageRequest page) {
        var response = userManagementFacade.searchUsers(request, page);
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
