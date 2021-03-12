package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.config.*;

public interface IConfigFacade {

    SearchConfigResponse searchConfig(SearchConfigRequest request);

    SaveConfigResponse saveConfig(SaveConfigRequest request);

}
