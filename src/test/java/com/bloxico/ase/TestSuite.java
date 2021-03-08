package com.bloxico.ase;

import com.bloxico.ase.userservice.config.security.AseSecurityServiceTest;
import com.bloxico.ase.userservice.config.security.PersistentJwtTokenStoreTest;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.filter.JwtAuthorizationFilterTest;
import com.bloxico.ase.userservice.service.address.impl.LocationServiceImplTest;
import com.bloxico.ase.userservice.service.artwork.impl.*;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.*;
import com.bloxico.ase.userservice.service.aws.impl.S3ServiceImplTest;
import com.bloxico.ase.userservice.service.document.impl.DocumentServiceImplTest;
import com.bloxico.ase.userservice.service.evaluation.impl.EvaluationServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthAccessTokenServiceImplTest;
import com.bloxico.ase.userservice.service.oauth.impl.OAuthClientDetailsServiceImplTest;
import com.bloxico.ase.userservice.service.token.impl.*;
import com.bloxico.ase.userservice.service.user.impl.*;
import com.bloxico.ase.userservice.util.AWSUtilTest;
import com.bloxico.ase.userservice.web.api.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
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
        CategoryServiceImplTest.class,
        MaterialServiceImplTest.class,
        MediumServiceImplTest.class,
        StyleServiceImplTest.class,
        EvaluationServiceImplTest.class,
        DocumentServiceImplTest.class,
        ArtistServiceImplTest.class,
        ArtworkGroupServiceImplTest.class,
        ArtworkServiceImplTest.class,

        // facade
        QuartzOperationsFacadeImplTest.class,
        UserRegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,
        LocationFacadeImplTest.class,
        UserManagementFacadeImplTest.class,
        ArtworkMetadataFacadeImplTest.class,
        EvaluationFacadeImplTest.class,
        ArtworkFacadeImplTest.class,
        AWSUtilTest.class,

        // filter
        JwtAuthorizationFilterTest.class,

        // api
        UserRegistrationApiTest.class,
        UserPasswordApiTest.class,
        UserProfileApiTest.class,
        LocationApiTest.class,
        UserManagementApiTest.class,
        ArtworkMetadataApiTest.class,
        ArtworkApiTest.class,
        EvaluationApiTest.class
})
public class TestSuite {
}
