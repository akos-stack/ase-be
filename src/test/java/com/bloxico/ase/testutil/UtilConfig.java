package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config.Type;
import com.bloxico.ase.userservice.repository.config.ConfigRepository;
import com.bloxico.ase.userservice.service.config.impl.ConfigServiceImpl;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.Util.randEnumConst;

@Component
public class UtilConfig {

    @Autowired private ConfigRepository configRepository;
    @Autowired private ConfigServiceImpl configService;

    public void deleteConfigById(Long id) {
        configRepository.deleteById(id);
    }

    public ConfigDto savedConfigDto() {
        return savedConfigDto(randEnumConst(Type.class), genUUID());
    }

    public ConfigDto savedConfigDto(Type type, String value) {
        return configService.saveOrUpdateConfig(genConfigDto(type, value));
    }

    public ConfigDto genConfigDto() {
        return genConfigDto(randEnumConst(Type.class), genUUID());
    }

    public ConfigDto genConfigDto(Type type, String value) {
        var configDto = new ConfigDto();
        configDto.setType(type);
        configDto.setValue(value);
        return configDto;
    }

    public SaveConfigRequest genSaveConfigRequest() {
        return genSaveConfigRequest(randEnumConst(Type.class), genUUID());
    }

    public SaveConfigRequest genSaveConfigRequest(Type type, String value) {
        return new SaveConfigRequest(type, value);
    }

}
