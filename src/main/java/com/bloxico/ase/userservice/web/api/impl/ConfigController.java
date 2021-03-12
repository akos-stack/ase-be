package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IConfigFacade;
import com.bloxico.ase.userservice.web.api.ConfigApi;
import com.bloxico.ase.userservice.web.model.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController implements ConfigApi {

    private final IConfigFacade configFacade;

    @Autowired
    public ConfigController(IConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    @Override
    public ResponseEntity<SearchConfigResponse> searchConfig(SearchConfigRequest request) {
        var response = configFacade.searchConfig(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveConfigResponse> saveConfig(SaveConfigRequest request) {
        var response = configFacade.saveConfig(request);
        return ResponseEntity.ok(response);
    }

}
