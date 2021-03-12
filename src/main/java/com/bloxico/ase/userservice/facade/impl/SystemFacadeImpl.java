package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.ISystemFacade;
import com.bloxico.ase.userservice.service.config.IConfigService;
import com.bloxico.ase.userservice.service.constant.IConstantService;
import com.bloxico.ase.userservice.web.model.config.*;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
@Transactional
public class SystemFacadeImpl implements ISystemFacade {

    private final IConstantService constantService;
    private final IConfigService configService;

    @Autowired
    public SystemFacadeImpl(IConstantService constantService,
                            IConfigService configService)
    {
        this.constantService = constantService;
        this.configService = configService;
    }

    @Override
    public SystemConstantsResponse systemConstants() {
        log.info("SystemFacadeImpl.systemConstants - start");
        var constants = constantService.constantsAsMap();
        var response = new SystemConstantsResponse(constants);
        log.info("SystemFacadeImpl.systemConstants - end");
        return response;
    }

    @Override
    public SearchConfigResponse searchConfig(SearchConfigRequest request) {
        log.debug("SystemFacadeImpl.searchConfig - start | request: {}", request);
        var configDto = configService.findConfigByType(request.getType());
        var response = new SearchConfigResponse(configDto);
        log.debug("SystemFacadeImpl.searchConfig - end | request: {}", request);
        return response;
    }

    @Override
    public SaveConfigResponse saveConfig(SaveConfigRequest request) {
        log.debug("SystemFacadeImpl.saveConfig - start | request: {}", request);
        var dto = MAPPER.toDto(request);
        var configDto = configService.saveOrUpdateConfig(dto);
        var response = new SaveConfigResponse(configDto);
        log.debug("SystemFacadeImpl.saveConfig - end | request: {}", request);
        return response;
    }

}
