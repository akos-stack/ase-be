package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.ISystemFacade;
import com.bloxico.ase.userservice.web.api.SystemApi;
import com.bloxico.ase.userservice.web.model.config.*;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController implements SystemApi {

    @Autowired
    private ISystemFacade systemFacade;

    @Override
    public ResponseEntity<SystemConstantsResponse> systemConstants() {
        var response = systemFacade.systemConstants();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SearchConfigResponse> searchConfig(SearchConfigRequest request) {
        var response = systemFacade.searchConfig(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveConfigResponse> saveConfig(SaveConfigRequest request) {
        var response = systemFacade.saveConfig(request);
        return ResponseEntity.ok(response);
    }

}
