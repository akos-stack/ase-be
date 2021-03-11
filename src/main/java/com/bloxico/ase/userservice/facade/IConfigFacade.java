package com.bloxico.ase.userservice.facade;


import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;

public interface IConfigFacade {

    SaveConfigResponse saveConfig(SaveConfigRequest request);

}
