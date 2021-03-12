package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.config.*;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;

public interface ISystemFacade {

    SystemConstantsResponse systemConstants();

    SearchConfigResponse searchConfig(SearchConfigRequest request);

    SaveConfigResponse saveConfig(SaveConfigRequest request);

}
