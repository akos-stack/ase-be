package com.bloxico.ase;

import com.bloxico.ase.userservice.config.security.AseSecurityServiceTest;
import com.bloxico.ase.userservice.config.security.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepositoryTest;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.token.PasswordResetTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.token.RegistrationTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.user.*;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.PasswordResetTokenServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.RolePermissionServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserPasswordServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserRegistrationServiceImplTest;
import com.bloxico.ase.userservice.web.api.UserManagementApiTest;
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
        RegistrationTokenRepositoryTest.class,
        PasswordResetTokenRepositoryTest.class,

        // config
        PersistentJwtTokenStoreTest.class,
        AseSecurityServiceTest.class,

        // service
        UserRegistrationServiceImplTest.class,
        UserPasswordServiceImplTest.class,
        UserProfileServiceImplTest.class,
        RolePermissionServiceImplTest.class,
        TokenBlacklistServiceImplTest.class,
        OAuthAccessTokenServiceImplTest.class,
        OAuthClientDetailsServiceImplTest.class,
        RegistrationTokenServiceImplTest.class,
        PasswordResetTokenServiceImplTest.class,

        // facade
        QuartzOperationsFacadeImplTest.class,
        UserRegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,
        UserManagementFacadeImplTest.class,

        // Run these individually for now
        // They stuck for some reason

        // filter
        // JwtAuthorizationFilterTest.class

        // api
        // UserRegistrationApiTest.class,
        // UserPasswordApiTest.class,
        // UserProfileApiTest.class,
        // UserManagementApiTest.class,
})
public class TestSuite {
}
