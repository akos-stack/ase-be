package com.bloxico.ase.userservice.service.config.impl;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config;
import com.bloxico.ase.userservice.repository.config.ConfigRepository;
import com.bloxico.ase.userservice.service.config.IConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ConfigServiceImpl implements IConfigService {

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public ConfigDto saveOrUpdateConfig(ConfigDto dto) {
        log.debug("ConfigServiceImpl.saveOrUpdateConfig - start | dto: {}", dto);
        requireNonNull(dto);
        var config = configRepository
                .findByType(dto.getType())
                .map(c -> updateConfig(c, dto))
                .orElseGet(() -> saveConfig(dto));
        var configDto = MAPPER.toDto(config);
        log.debug("ConfigServiceImpl.saveOrUpdateConfig - end | dto: {}", dto);
        return configDto;
    }

    private Config updateConfig(Config config, ConfigDto dto) {
        config.setType(dto.getType());
        config.setValue(dto.getValue());
        return configRepository.saveAndFlush(config);
    }

    private Config saveConfig(ConfigDto dto) {
        var config = MAPPER.toEntity(dto);
        return configRepository.saveAndFlush(config);
    }

}
