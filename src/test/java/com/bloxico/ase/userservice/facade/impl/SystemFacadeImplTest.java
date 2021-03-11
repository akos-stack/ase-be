package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.UtilSystem.assertValidSystemConstants;

public class SystemFacadeImplTest extends AbstractSpringTest {

    @Autowired private SystemFacadeImpl systemFacade;

    @Test
    public void systemConstants() {
        var constants = systemFacade.systemConstants();
        assertValidSystemConstants(constants.getConstants());
    }

}
