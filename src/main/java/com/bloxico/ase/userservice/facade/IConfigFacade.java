package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
import com.bloxico.ase.userservice.web.model.config.SearchConfigResponse;

import static com.bloxico.ase.userservice.entity.config.Config.Type;

public interface IConfigFacade {

    SearchConfigResponse searchConfig(Type type);

    SaveConfigResponse saveConfig(SaveConfigRequest request);

}
