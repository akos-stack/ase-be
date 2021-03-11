package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config.Type;
import com.bloxico.ase.userservice.repository.config.ConfigRepository;
import com.bloxico.ase.userservice.service.config.impl.ConfigServiceImpl;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.config.Config.Type.QUOTATION_PACKAGE_MIN_EVALUATIONS;

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

    public ConfigDto savedConfigDto(Type type, Object value) {
        return configService.saveOrUpdateConfig(genConfigDto(type, value));
    }

    public ConfigDto savedQuotationPackageMinEvaluatorsConfigDto() {
        return savedConfigDto(QUOTATION_PACKAGE_MIN_EVALUATIONS, genPosInt(50));
    }

    public ConfigDto savedQuotationPackageMinEvaluatorsConfigDto(int value) {
        return savedConfigDto(QUOTATION_PACKAGE_MIN_EVALUATIONS, value);
    }

    public ConfigDto genConfigDto() {
        return genConfigDto(randEnumConst(Type.class), genUUID());
    }

    public ConfigDto genConfigDto(Type type, Object value) {
        var configDto = new ConfigDto();
        configDto.setType(type);
        configDto.setValue(value.toString());
        return configDto;
    }

    public SaveConfigRequest genSaveConfigRequest() {
        return genSaveConfigRequest(randEnumConst(Type.class), genUUID());
    }

    public SaveConfigRequest genSaveConfigRequest(Type type, Object value) {
        return new SaveConfigRequest(type, value);
    }

}
