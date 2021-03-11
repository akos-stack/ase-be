package com.bloxico.ase.userservice.service.config.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilConfig;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.exception.ConfigException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilConfig utilConfig;
    @Autowired private ConfigServiceImpl configService;

    @Test
    @WithMockCustomUser
    public void findConfigByType_nullType() {
        assertThrows(
                NullPointerException.class,
                () -> configService.findConfigByType(null));
    }

    @Test
    @WithMockCustomUser
    public void findConfigByType_notFound() {
        var config = utilConfig.savedConfigDto();
        utilConfig.deleteConfigById(config.getId());
        assertThrows(
                ConfigException .class,
                () -> configService.findConfigByType(config.getType()));
    }

    @Test
    @WithMockCustomUser
    public void findConfigByType() {
        var config = utilConfig.savedConfigDto();
        var foundConfig = configService.findConfigByType(config.getType());
        assertEquals(config, foundConfig);
    }

    @Test
    @WithMockCustomUser
    public void saveOrUpdateConfig_nullConfig() {
        assertThrows(
                NullPointerException.class,
                () -> configService.saveOrUpdateConfig(null));
    }

    @Test
    @WithMockCustomUser
    public void saveOrUpdateConfig_nullType() {
        var dto = utilConfig.genConfigDto();
        dto.setType(null);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> configService.saveOrUpdateConfig(dto));
    }

    @Test
    @WithMockCustomUser
    public void saveOrUpdateConfig_nullValue() {
        var dto = utilConfig.genConfigDto();
        dto.setValue(null);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> configService.saveOrUpdateConfig(dto));
    }

    @Test
    @WithMockCustomUser
    public void saveOrUpdateConfig() {
        var dto = utilConfig.genConfigDto();
        var config = configService.saveOrUpdateConfig(dto);
        assertNotNull(config);
        assertNotNull(config.getId());
        assertEquals(dto.getType(), config.getType());
        assertEquals(dto.getValue(), config.getValue());
    }

}
