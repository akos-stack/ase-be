package com.bloxico.ase.userservice.service.constant;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.service.constant.impl.ConstantServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.UtilSystem.assertValidSystemConstants;

public class ConstantServiceImplTest extends AbstractSpringTest {

    @Autowired private ConstantServiceImpl constantService;

    @Test
    public void constantsAsMap() {
        assertValidSystemConstants(constantService.constantsAsMap());
    }

}
