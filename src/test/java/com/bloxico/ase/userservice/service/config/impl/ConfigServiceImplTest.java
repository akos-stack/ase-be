package com.bloxico.ase.userservice.service.config.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilConfig;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilConfig utilConfig;
    @Autowired private ConfigServiceImpl configService;

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
        var saveOrUpdatedConfig = configService.saveOrUpdateConfig(dto);
        assertNotNull(saveOrUpdatedConfig);
        assertNotNull(saveOrUpdatedConfig.getId());
        assertEquals(dto.getType(), saveOrUpdatedConfig.getType());
        assertEquals(dto.getValue(), saveOrUpdatedConfig.getValue());
    }

}
