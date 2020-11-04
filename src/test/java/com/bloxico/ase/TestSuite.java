package com.bloxico.ase;

import com.bloxico.ase.userservice.config.StartupConfigTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.repository.*;
import com.bloxico.ase.userservice.service.token.impl.JwtServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // entity
        BaseEntityTest.class,

        // repository
        PermissionRepositoryTest.class,
        RoleRepositoryTest.class,
        UserProfileRepositoryTest.class,
        OwnerRepositoryTest.class,
        EvaluatorRepositoryTest.class,
        BlacklistedJwtRepositoryTest.class,

        // config
        StartupConfigTest.class,

        // service
        UserProfileServiceImplTest.class,
        JwtServiceImplTest.class
})
public class TestSuite {
}
