package com.bloxico;

import com.bloxico.userservice.config.oauth2.CoinUserDetailsServiceTest;
import com.bloxico.userservice.facade.impl.DeleteTokenQuartzFacadeImplTest;
import com.bloxico.userservice.facade.impl.RegistrationFacadeImplTest;
import com.bloxico.userservice.facade.impl.UserPasswordFacadeImplTest;
import com.bloxico.userservice.facade.impl.UserProfileFacadeImplTest;
import com.bloxico.userservice.services.oauth.impl.OauthTokenServiceImplTest;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImplTest;
import com.bloxico.userservice.services.user.impl.UserPasswordServiceImplTest;
import com.bloxico.userservice.services.user.impl.UserProfileServiceImplTest;
import com.bloxico.userservice.services.user.impl.UserRegistrationServiceImplTest;
import com.bloxico.userservice.services.user.impl.UserServiceImplTest;
import com.bloxico.userservice.web.api.UserPasswordControllerTest;
import com.bloxico.userservice.web.api.UserProfileControllerTest;
import com.bloxico.userservice.web.api.UserRegistrationControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserPasswordControllerTest.class,
        UserRegistrationControllerTest.class,
        UserProfileControllerTest.class,
        RegistrationFacadeImplTest.class,
        UserPasswordFacadeImplTest.class,
        UserProfileFacadeImplTest.class,
        VerificationTokenServiceImplTest.class,
        UserServiceImplTest.class,
        UserRegistrationServiceImplTest.class,
        UserPasswordServiceImplTest.class,
        UserProfileServiceImplTest.class,
        DeleteTokenQuartzFacadeImplTest.class,
        CoinUserDetailsServiceTest.class,
        OauthTokenServiceImplTest.class,
})
public class TestSuite {
}
