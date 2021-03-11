package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilConfig;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.config.Config.Type;
import com.bloxico.ase.userservice.exception.ConfigException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.Util.randEnumConst;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilConfig utilConfig;
    @Autowired private ConfigFacadeImpl configFacade;

    @Test
    @WithMockCustomUser
    public void searchConfig_nullType() {
        assertThrows(
                NullPointerException.class,
                () -> configFacade.searchConfig(null));
    }

    @Test
    @WithMockCustomUser
    public void searchConfig_notFound() {
        var config = utilConfig.savedConfigDto();
        utilConfig.deleteConfigById(config.getId());
        assertThrows(
                ConfigException.class,
                () -> configFacade.searchConfig(config.getType()));
    }

    @Test
    @WithMockCustomUser
    public void searchConfig() {
        var config = utilConfig.savedConfigDto();
        var foundConfig = configFacade
                .searchConfig(config.getType())
                .getConfig();
        assertNotNull(foundConfig);
        assertEquals(config.getId(), foundConfig.getId());
        assertEquals(config.getType(), foundConfig.getType());
        assertEquals(config.getValue(), foundConfig.getValue());
    }

    @Test
    @WithMockCustomUser
    public void saveConfig_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> configFacade.saveConfig(null));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig_nullType() {
        var request = utilConfig.genSaveConfigRequest(null, genUUID());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> configFacade.saveConfig(request));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig_nullValue() {
        var request = utilConfig.genSaveConfigRequest(randEnumConst(Type.class), null);
        assertThrows(
                DataIntegrityViolationException.class,
                () -> configFacade.saveConfig(request));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig() {
        var request = utilConfig.genSaveConfigRequest();
        var config = configFacade
                .saveConfig(request)
                .getConfig();
        assertNotNull(config);
        assertNotNull(config.getId());
        assertEquals(request.getType(), config.getType());
        assertEquals(request.getValue(), config.getValue());
    }

}
