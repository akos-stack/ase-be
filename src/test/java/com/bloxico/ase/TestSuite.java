package com.bloxico.ase;

import com.bloxico.ase.userservice.config.security.AseSecurityServiceTest;
import com.bloxico.ase.userservice.config.security.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.repository.address.*;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepositoryTest;
import com.bloxico.ase.userservice.repository.token.*;
import com.bloxico.ase.userservice.repository.user.*;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepositoryTest;
import com.bloxico.ase.userservice.repository.user.profile.EvaluatorRepositoryTest;
import com.bloxico.ase.userservice.service.address.impl.LocationServiceImplTest;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.*;
import com.bloxico.ase.userservice.service.user.impl.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // entity
        BaseEntityTest.class,

        // repository
        CountryRepositoryTest.class,
        CityRepositoryTest.class,
        LocationRepositoryTest.class,
        PermissionRepositoryTest.class,
        RoleRepositoryTest.class,
        UserRepositoryTest.class,
        ArtOwnerRepositoryTest.class,
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
        UserServiceImplTest.class,
        UserProfileServiceImplTest.class,
        RolePermissionServiceImplTest.class,
        TokenBlacklistServiceImplTest.class,
        OAuthAccessTokenServiceImplTest.class,
        OAuthClientDetailsServiceImplTest.class,
        RegistrationTokenServiceImplTest.class,
        PasswordResetTokenServiceImplTest.class,
        LocationServiceImplTest.class,
        PendingEvaluatorServiceImplTest.class,
        S3ServiceImplTest.class,

        // facade
        QuartzOperationsFacadeImplTest.class,
        UserRegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,
        LocationFacadeImplTest.class,
        UserManagementFacadeImplTest.class,

        // Run these individually for now
        // They stuck for some reason

        // filter
        // JwtAuthorizationFilterTest.class

        // api
        // UserRegistrationApiTest.class,
        // UserPasswordApiTest.class,
        // UserProfileApiTest.class,
        // LocationApiTest.class,
        // UserManagementApiTest.class,
})
public class TestSuite {
}
