package com.bloxico.ase.userservice.service.config;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config.Type;

public interface IConfigService {

    ConfigDto findConfigByType(Type type);

    ConfigDto saveOrUpdateConfig(ConfigDto configDto);

}
