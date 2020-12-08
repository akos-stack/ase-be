package com.bloxico.ase;

import com.bloxico.ase.userservice.config.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.facade.impl.UserPasswordFacadeImplTest;
import com.bloxico.ase.userservice.facade.impl.UserProfileFacadeImplTest;
import com.bloxico.ase.userservice.facade.impl.UserRegistrationFacadeImplTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepositoryTest;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.user.*;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.RolePermissionServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserPasswordServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserRegistrationServiceImplTest;
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
        BlacklistedTokenRepositoryTest.class,
        OAuthAccessTokenRepositoryTest.class,
        OAuthClientDetailsRepositoryTest.class,

        // config
        PersistentJwtTokenStoreTest.class,

        // service
        UserRegistrationServiceImplTest.class,
        UserPasswordServiceImplTest.class,
        UserProfileServiceImplTest.class,
        RolePermissionServiceImplTest.class,
        TokenBlacklistServiceImplTest.class,
        OAuthAccessTokenServiceImplTest.class,
        OAuthClientDetailsServiceImplTest.class,

        // facade
        UserRegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,

        // Run these individually for now
        // They stuck for some reason

        // api
        // UserRegistrationApiTest.class,
        // UserPasswordApiTest.class,
        // UserProfileApiTest.class,
        // JwtAuthorizationFilterTest.class
})
public class TestSuite {
}
