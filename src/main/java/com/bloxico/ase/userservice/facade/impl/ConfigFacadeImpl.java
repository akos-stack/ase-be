package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IConfigFacade;
import com.bloxico.ase.userservice.service.config.IConfigService;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigResponse;
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
    public SaveConfigResponse saveConfig(SaveConfigRequest request) {
        log.debug("ConfigFacadeImpl.saveConfig - start | request: {}", request);
        var dto = MAPPER.toDto(request);
        var configDto = configService.saveOrUpdateConfig(dto);
        var response = new SaveConfigResponse(configDto);
        log.debug("ConfigFacadeImpl.saveConfig - end | request: {}", request);
        return response;
    }

}
