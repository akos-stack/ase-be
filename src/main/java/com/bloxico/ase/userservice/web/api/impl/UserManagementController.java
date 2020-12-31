package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.facade.IUserManagementFacade;
import com.bloxico.ase.userservice.web.api.UserManagementApi;
import com.bloxico.ase.userservice.web.model.user.ArrayUserProfileDataResponse;
import com.bloxico.ase.userservice.web.model.user.BlacklistTokensRequest;
import com.bloxico.ase.userservice.web.model.user.DisableUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static com.bloxico.ase.userservice.util.PrincipalUtil.extractId;

@RestController
public class UserManagementController implements UserManagementApi {

    @Autowired
    private IUserManagementFacade userManagementFacade;

    @Override
    public ResponseEntity<ArrayUserProfileDataResponse> searchUsers(@Valid String email, @Valid Role.UserRole role, @Valid int page, @Valid int size, @Valid String sort) {
        var arrayUserProfileDataResponse = userManagementFacade.searchUsers(email, role, page, size, sort);
        return ResponseEntity.ok(arrayUserProfileDataResponse);
    }

    @Override
    public ResponseEntity<Void> disableUser(@Valid DisableUserRequest request, Principal principal) {
        var id = extractId(principal);
        userManagementFacade.disableUser(request.getUserId(), id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> blacklistTokens(@Valid BlacklistTokensRequest request, Principal principal) {
        var id = extractId(principal);
        userManagementFacade.blacklistTokens(request.getUserId(), id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
