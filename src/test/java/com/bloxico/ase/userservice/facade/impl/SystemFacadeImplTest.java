package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilSystem;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.config.Config;
import com.bloxico.ase.userservice.exception.ConfigException;
import com.bloxico.ase.userservice.web.model.config.SearchConfigRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.Util.randEnumConst;
import static com.bloxico.ase.testutil.UtilSystem.assertValidSystemConstants;
import static org.junit.jupiter.api.Assertions.*;

public class SystemFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilSystem utilSystem;
    @Autowired private SystemFacadeImpl systemFacade;

    @Test
    public void systemConstants() {
        var constants = systemFacade.systemConstants();
        assertValidSystemConstants(constants.getConstants());
    }

    @Test
    @WithMockCustomUser
    public void searchConfig_nullType() {
        assertThrows(
                NullPointerException.class,
                () -> systemFacade.searchConfig(null));
    }

    @Test
    @WithMockCustomUser
    public void searchConfig_notFound() {
        var config = utilSystem.savedConfigDto();
        utilSystem.deleteConfigById(config.getId());
        assertThrows(
                ConfigException.class,
                () -> systemFacade.searchConfig(new SearchConfigRequest(config.getType())));
    }

    @Test
    @WithMockCustomUser
    public void searchConfig() {
        var config = utilSystem.savedConfigDto();
        var foundConfig = systemFacade
                .searchConfig(new SearchConfigRequest(config.getType()))
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
                () -> systemFacade.saveConfig(null));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig_nullType() {
        var request = utilSystem.genSaveConfigRequest(null, genUUID());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> systemFacade.saveConfig(request));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig_nullValue() {
        var request = utilSystem.genSaveConfigRequest(randEnumConst(Config.Type.class), null);
        assertThrows(
                NullPointerException.class,
                () -> systemFacade.saveConfig(request));
    }

    @Test
    @WithMockCustomUser
    public void saveConfig() {
        var request = utilSystem.genSaveConfigRequest();
        var config = systemFacade
                .saveConfig(request)
                .getConfig();
        assertNotNull(config);
        assertNotNull(config.getId());
        assertEquals(request.getType(), config.getType());
        assertEquals(request.getValue(), config.getValue());
    }

}
