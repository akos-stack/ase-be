package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IConfigFacade;
import com.bloxico.ase.userservice.web.api.ConfigApi;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ConfigController implements ConfigApi {

    private final IConfigFacade configFacade;

    @Autowired
    public ConfigController(IConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    @Override
    public ResponseEntity<SaveConfigResponse> saveConfig(@Valid SaveConfigRequest request) {
        var response = configFacade.saveConfig(request);
        return ResponseEntity.ok(response);
    }

}
