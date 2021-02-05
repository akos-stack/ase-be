package com.bloxico.ase;

import com.bloxico.ase.userservice.config.security.AseSecurityServiceTest;
import com.bloxico.ase.userservice.config.security.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.entity.BaseEntityTest;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.repository.address.CityRepositoryTest;
import com.bloxico.ase.userservice.repository.address.CountryRepositoryTest;
import com.bloxico.ase.userservice.repository.address.LocationRepositoryTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepositoryTest;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.token.PasswordResetTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.token.RegistrationTokenRepositoryTest;
import com.bloxico.ase.userservice.repository.user.PermissionRepositoryTest;
import com.bloxico.ase.userservice.repository.user.RoleRepositoryTest;
import com.bloxico.ase.userservice.repository.user.UserRepositoryTest;
import com.bloxico.ase.userservice.repository.user.profile.ArtOwnerRepositoryTest;
import com.bloxico.ase.userservice.repository.user.profile.EvaluatorRepositoryTest;
import com.bloxico.ase.userservice.service.address.impl.LocationServiceImplTest;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkCategoryServiceImplTest;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkMaterialServiceImplTest;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkMediumServiceImplTest;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkStyleServiceImplTest;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.PasswordResetTokenServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.RegistrationTokenServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.TokenBlacklistServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.RolePermissionServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImplTest;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImplTest;
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
        ArtworkCategoryServiceImplTest.class,
        ArtworkMaterialServiceImplTest.class,
        ArtworkMediumServiceImplTest.class,
        ArtworkStyleServiceImplTest.class,

        // facade
        QuartzOperationsFacadeImplTest.class,
        UserRegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,
        LocationFacadeImplTest.class,
        UserManagementFacadeImplTest.class,
        ArtworkMetadataFacadeImplTest.class,

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
        // ArtworkMetadataManagementApiTest.class,
        // ArtworkMetadataApiTest.class
})
public class TestSuite {
}
