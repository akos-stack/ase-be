package com.bloxico.ase;

import com.bloxico.ase.userservice.config.security.AseSecurityServiceTest;
import com.bloxico.ase.userservice.config.security.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.filter.JwtAuthorizationFilterTest;
import com.bloxico.ase.userservice.service.address.impl.LocationServiceImplTest;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.*;
import com.bloxico.ase.userservice.service.user.impl.*;
import com.bloxico.ase.userservice.web.api.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        // entity
        BaseEntityTest.class,

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

        // filter
        JwtAuthorizationFilterTest.class,

        // api
        UserRegistrationApiTest.class,
        UserPasswordApiTest.class,
        UserProfileApiTest.class,
        LocationApiTest.class,
        UserManagementApiTest.class,
})
public class TestSuite {
}
