package com.bloxico.ase.userservice.web.api.impl;

import com.bloxico.ase.userservice.facade.IConfigFacade;
import com.bloxico.ase.userservice.web.api.ConfigApi;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import com.bloxico.ase.userservice.web.model.config.SearchConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.bloxico.ase.userservice.entity.config.Config.Type;

@RestController
public class ConfigController implements ConfigApi {

    private final IConfigFacade configFacade;

    @Autowired
    public ConfigController(IConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    @Override
    public ResponseEntity<SearchConfigResponse> searchConfig(@Valid Type type) {
        var response = configFacade.searchConfig(type);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SaveConfigResponse> saveConfig(@Valid SaveConfigRequest request) {
        var response = configFacade.saveConfig(request);
        return ResponseEntity.ok(response);
    }

}
