package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.ISystemFacade;
import com.bloxico.ase.userservice.service.constant.IConstantService;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SystemFacadeImpl implements ISystemFacade {

    private final IConstantService constantService;

    @Autowired
    public SystemFacadeImpl(IConstantService constantService) {
        this.constantService = constantService;
    }

    @Override
    public SystemConstantsResponse systemConstants() {
        log.info("SystemFacadeImpl.systemConstants - start");
        var constants = constantService.constantsAsMap();
        var response = new SystemConstantsResponse(constants);
        log.info("SystemFacadeImpl.systemConstants - end");
        return response;
    }

}
