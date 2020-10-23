package com.bloxico.userservice.util;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.entities.token.PasswordResetToken;
import com.bloxico.userservice.entities.token.VerificationToken;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.token.PasswordTokenRepository;
import com.bloxico.userservice.repository.token.VerificationTokenRepository;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.services.token.impl.PasswordTokenServiceImpl;
import com.bloxico.userservice.services.token.impl.VerificationTokenServiceImpl;
import com.bloxico.userservice.services.user.IUserRegistrationService;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.util.mappers.RegistrationRequestMapper;
import com.bloxico.userservice.web.error.ErrorCodes;
import com.bloxico.userservice.web.model.registration.RegistrationRequest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static io.restassured.RestAssured.given;

/**
 * This class will contain utility methods that will be used when initiating tests in order to have valid testing environment
 */
@Component("mockUtilOld")
public class MockUtil {

    @Autowired
    private IUserRegistrationService userRegistrationService;

    @Autowired
    private IUserService userService;

    @Autowired
    private VerificationTokenServiceImpl verificationTokenService;

    @Autowired
    private PasswordTokenServiceImpl passwordTokenService;

    @Autowired
    private CoinUserRepository coinUserRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private Environment env;

    public CoinUserDto insertMockUser(String email) {
        RegistrationRequest registrationRequest = createMockRegistrationRequest(email);

        CoinUserDto coinUserDto = userRegistrationService.registerDisabledUser(RegistrationRequestMapper.INSTANCE.requestToDto(registrationRequest));

        verificationTokenService.createTokenForUser(coinUserDto.getId());

        return coinUserDto;
    }

    public String enableAndAuthenticateUser(String email, String password) {
        enableUser(email);

        String accessToken = authenticateUser(email, password);

        return accessToken;
    }


    public void enableUser(String email) {
        CoinUser coinUser = findUserByEmail(email);
        userRegistrationService.enableUser(coinUser.getId());
    }

    public String authenticateUser(String email, String password) {

        String clientId = env.getProperty("oauth2.client.id");
        String secret = env.getProperty("oauth2.secret");
        String apiUrl = env.getProperty("api.url");

        Map<String, String> map = new HashMap<>();
        map.put("username", email);
        map.put("password", password);
        map.put("grant_type", "password");
        map.put("scope", "access_profile");

        String response =
                given()
                        .contentType(ContentType.URLENC)
                        .accept(ContentType.JSON)
                        .formParams(map)
                        .auth()
                        .preemptive()
                        .basic(clientId, secret)
                        .when()
                        .post(apiUrl + "/oauth/token")
                        .asString();

        JsonPath jsonPath = new JsonPath(response);

        return jsonPath.getString("access_token");
    }

    public String authenticateWithCLientCredentials(String clientId, String secret) {

        String apiUrl = env.getProperty("api.url");

        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");

        String response =
                given()
                        .contentType(ContentType.URLENC)
                        .accept(ContentType.JSON)
                        .formParams(map)
                        .auth()
                        .preemptive()
                        .basic(clientId, secret)
                        .when()
                        .post(apiUrl + "/oauth/token")
                        .asString();

        JsonPath jsonPath = new JsonPath(response);

        return jsonPath.getString("access_token");
    }

    public RegistrationRequest createMockRegistrationRequest(String email) {

        RegistrationRequest mockUser = new RegistrationRequest();
        mockUser.setEmail(email);
        mockUser.setCity("Belgrade");
        mockUser.setRegionName("Serbia");
        mockUser.setPassword("randomPassword1!");
        mockUser.setMatchPassword("randomPassword1!");

        return mockUser;
    }


    public VerificationToken expireVerificationTokenForUser(String email) {
        CoinUser coinUser = findUserByEmail(email);

        Optional<VerificationToken> op = verificationTokenRepository.findByUserId(coinUser.getId());

        VerificationToken token = op.get();
        token.setExpiryDate(DateUtil.addMinutesToGivenDate(LocalDateTime.now(), -200));

        verificationTokenRepository.save(token);

        return token;
    }

    public PasswordResetToken expirePasswordTokenForUser(String email) {
        CoinUser coinUser = findUserByEmail(email);

        Optional<PasswordResetToken> op = passwordTokenRepository.findByUserId(coinUser.getId());
        PasswordResetToken token = op.get();

        token.setExpiryDate(DateUtil.addMinutesToGivenDate(LocalDateTime.now(), -200));
        passwordTokenRepository.save(token);

        return token;
    }

    public static class Constants {
        public static final String MOCK_USER_EMAIL = "mockuser@noreply.com";
        public static final String MOCK_USER_PASSWORD = "randomPassword1!";

        public static final String MOCK_PARTNER_CLIENT_ID = "mock-partner-service";
        public static final String MOCK_PARTNER_SECRET = "Zu4ZyHqsnh39P6yy";

        public static final String MOCK_BLOCKCHAIN_CLIENT_ID = "mock-blockchain-service";
        public static final String MOCK_BLOCKCHAIN_CLIENT_SECRET = "Pass123!";


        public static final String DEFAULT_ADDRESS_HASH = "eDjumaPMLHbhVujvSsoN8iqZmRrqqAUT5D";


    }

    public CoinUser findUserByEmail(String email) {
        Optional<CoinUser> op = coinUserRepository.findByEmailIgnoreCase(email);
        CoinUser coinUser = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        return coinUser;
    }
}
