package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config;
import com.bloxico.ase.userservice.repository.config.ConfigRepository;
import com.bloxico.ase.userservice.service.config.impl.ConfigServiceImpl;
import com.bloxico.ase.userservice.util.EnumConstants;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.config.Config.Type.QUOTATION_PACKAGE_MIN_EVALUATIONS;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertTrue;

@Component
public class UtilSystem {

    @Autowired private ConfigRepository configRepository;
    @Autowired private ConfigServiceImpl configService;

    public void deleteConfigById(Long id) {
        configRepository.deleteById(id);
    }

    public ConfigDto savedConfigDto() {
        return savedConfigDto(randEnumConst(Config.Type.class), genUUID());
    }

    public ConfigDto savedConfigDto(Config.Type type, Object value) {
        return configService.saveOrUpdateConfig(genConfigDto(type, value));
    }

    public ConfigDto savedQuotationPackageMinEvaluatorsConfigDto() {
        return savedConfigDto(QUOTATION_PACKAGE_MIN_EVALUATIONS, genPosInt(50));
    }

    public ConfigDto savedQuotationPackageMinEvaluatorsConfigDto(int value) {
        return savedConfigDto(QUOTATION_PACKAGE_MIN_EVALUATIONS, value);
    }

    public ConfigDto genConfigDto() {
        return genConfigDto(randEnumConst(Config.Type.class), genUUID());
    }

    public ConfigDto genConfigDto(Config.Type type, Object value) {
        var configDto = new ConfigDto();
        configDto.setType(type);
        configDto.setValue(value.toString());
        return configDto;
    }

    public SaveConfigRequest genSaveConfigRequest() {
        return genSaveConfigRequest(randEnumConst(Config.Type.class), genUUID());
    }

    public SaveConfigRequest genSaveConfigRequest(Config.Type type, Object value) {
        return new SaveConfigRequest(type, value);
    }

    public static void assertValidSystemConstants(SystemConstantsResponse response) {
        assertValidSystemConstants(response.getConstants());
    }

    public static void assertValidSystemConstants(Map<String, Object> systemConstants) {
        var enumConstants = Arrays
                .stream(EnumConstants.values())
                .map(EnumConstants::name)
                .collect(toSet());
        assertTrue(systemConstants.keySet().containsAll(enumConstants));
        assertTrue(systemConstants.containsKey("PERMISSION"));
    }

}
