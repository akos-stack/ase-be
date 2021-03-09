package com.bloxico.ase.userservice.service.config;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;

public interface IConfigService {

    ConfigDto saveOrUpdateConfig(ConfigDto configDto);

}
