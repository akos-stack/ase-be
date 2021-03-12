package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IConfigFacade;
import com.bloxico.ase.userservice.service.config.IConfigService;
import com.bloxico.ase.userservice.web.model.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
@Transactional
public class ConfigFacadeImpl implements IConfigFacade {

    private final IConfigService configService;

    @Autowired
    public ConfigFacadeImpl(IConfigService configService) {
        this.configService = configService;
    }

    @Override
    public SearchConfigResponse searchConfig(SearchConfigRequest request) {
        log.debug("ConfigFacadeImpl.searchConfig - start | request: {}", request);
        var configDto = configService.findConfigByType(request.getType());
        var response = new SearchConfigResponse(configDto);
        log.debug("ConfigFacadeImpl.searchConfig - end | request: {}", request);
        return response;
    }

    @Override
    public SaveConfigResponse saveConfig(SaveConfigRequest request) {
        log.debug("ConfigFacadeImpl.saveConfig - start | request: {}", request);
        var dto = MAPPER.toDto(request);
        var configDto = configService.saveOrUpdateConfig(dto);
        var response = new SaveConfigResponse(configDto);
        log.debug("ConfigFacadeImpl.saveConfig - end | request: {}", request);
        return response;
    }

}
