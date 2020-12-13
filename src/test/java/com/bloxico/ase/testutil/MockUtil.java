package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.facade.impl.UserPasswordFacadeImpl;
import com.bloxico.ase.userservice.facade.impl.UserProfileFacadeImpl;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_CONFIRM_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;

@Component
public class MockUtil {

    public static final String ERROR_CODE = "errorCode";

    @Value("${api.url}")
    private String API_URL;

    @Value("${oauth2.client.id}")
    private String OAUTH2_CLIENT_ID;

    @Value("${oauth2.secret}")
    private String OAUTH2_SECRET;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileServiceImpl userProfileService;
    private final UserPasswordFacadeImpl userPasswordFacade;
    private final OAuthAccessTokenRepository oAuthAccessTokenRepository;
    private final TokenRepository tokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final UserProfileFacadeImpl userProfileFacade;

    @Autowired
    public MockUtil(PasswordEncoder passwordEncoder,
                    RoleRepository roleRepository,
                    UserProfileRepository userProfileRepository,
                    UserProfileServiceImpl userProfileService,
                    UserPasswordFacadeImpl userPasswordFacade,
                    OAuthAccessTokenRepository oAuthAccessTokenRepository,
                    TokenRepository tokenRepository,
                    BlacklistedTokenRepository blacklistedTokenRepository,
                    UserProfileFacadeImpl userProfileFacade)
    {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
        this.userPasswordFacade = userPasswordFacade;
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.tokenRepository = tokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.userProfileFacade = userProfileFacade;
    }

    public UserProfile savedAdmin() {
        return savedAdmin("admin");
    }

    public UserProfile savedAdmin(String password) {
        return savedAdmin("admin@mail.com", password);
    }

    public UserProfile savedAdmin(String email, String password) {
        return userProfileRepository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    var user = new UserProfile();
                    user.setName(email.split("@")[0]);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setEmail(email);
                    user.setLocked(false);
                    user.setEnabled(true);
                    user.addRole(roleRepository.getAdminRole());
                    return userProfileRepository.saveAndFlush(user);
                });
    }

    public UserProfile savedUserProfile() {
        return savedUserProfile("foobar");
    }

    public UserProfile savedUserProfile(String password) {
        return savedUserProfile("foobar@mail.com", password);
    }

    public UserProfile savedUserProfile(String email, String password) {
        return userProfileRepository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    var user = new UserProfile();
                    user.setName(email.split("@")[0]);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setEmail(email);
                    user.setLocked(false);
                    user.setEnabled(true);
                    user.addRole(roleRepository.getUserRole());
                    return userProfileRepository.saveAndFlush(user);
                });
    }

    public UserProfileDto savedUserProfileDto() {
        return MAPPER.toDto(savedUserProfile());
    }

    public Token savedToken(Token.Type type) {
        return savedToken(type, uuid());
    }

    public Token savedToken(Token.Type type, String value) {
        var adminId = savedAdmin().getId();
        var token = new Token();
        token.setUserId(adminId);
        token.setValue(value);
        token.setType(type);
        token.setExpiryDate(notExpired());
        token.setCreatorId(adminId);
        return tokenRepository.saveAndFlush(token);
    }

    public Token savedExpiredToken(Token.Type type) {
        return savedExpiredToken(type, uuid());
    }

    public Token savedExpiredToken(Token.Type type, String value) {
        var userId = savedUserProfile().getId();
        var token = new Token();
        token.setUserId(userId);
        token.setValue(value);
        token.setType(type);
        token.setExpiryDate(expired());
        token.setCreatorId(userId);
        return tokenRepository.saveAndFlush(token);
    }

    public TokenDto savedExpiredTokenDto(Token.Type type) {
        return MAPPER.toDto(savedExpiredToken(type));
    }

    public OAuthAccessToken savedOauthToken(String email) {
        var token = new OAuthAccessToken();
        token.setTokenId(uuid());
        token.setUserName(email);
        token.setExpiration(notExpired());
        return oAuthAccessTokenRepository.saveAndFlush(token);
    }

    public OAuthAccessTokenDto savedOauthTokenDto(String email) {
        return MAPPER.toDto(savedOauthToken(email));
    }

    public OAuthAccessToken savedExpiredOauthToken(String email) {
        var token = new OAuthAccessToken();
        token.setTokenId(uuid());
        token.setUserName(email);
        token.setExpiration(expired());
        return oAuthAccessTokenRepository.saveAndFlush(token);
    }

    public OAuthAccessTokenDto savedExpiredOauthTokenDto(String email) {
        return MAPPER.toDto(savedExpiredOauthToken(email));
    }

    public BlacklistedToken getBlacklistedToken(String value) {
        return blacklistedTokenRepository
                .findAll()
                .stream()
                .filter(t -> t.getToken().equals(value))
                .findAny()
                .orElseThrow();
    }

    public BlacklistedToken savedBlacklistedToken() {
        var adminId = savedAdmin().getId();
        var user = savedUserProfile();
        var token = savedOauthTokenDto(user.getEmail());
        userProfileFacade.blacklistTokens(user.getId(), adminId);
        return getBlacklistedToken(token.getTokenId());
    }

    public BlacklistedToken savedExpiredBlacklistedToken() {
        var adminId = savedAdmin().getId();
        var user = savedUserProfile();
        var token = savedExpiredOauthTokenDto(user.getEmail());
        userProfileFacade.blacklistTokens(user.getId(), adminId);
        return getBlacklistedToken(token.getTokenId());
    }

    public static void copyBaseEntityData(BaseEntity from, BaseEntity to) {
        to.setCreatorId(from.getCreatorId());
        to.setUpdaterId(from.getUpdaterId());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());
    }

    public OAuthAccessTokenDto toOAuthAccessTokenDto(UserProfile userProfile, String token) {
        return toOAuthAccessTokenDto(userProfile.getEmail(), token);
    }

    public OAuthAccessTokenDto toOAuthAccessTokenDto(String email, String token) {
        return new OAuthAccessTokenDto(
                token,
                null, null,
                email,
                "appId",
                null,
                notExpired(),
                uuid());
    }

    @lombok.Value
    public static class Registration {
        long id;
        String email, password, token;
    }

    public Registration doRegistration() {
        String email = "passwordMatches@mail.com", pass = "Password1!";
        String token = given()
                .contentType(JSON)
                .body(new RegistrationRequest(email, pass, pass))
                .post(API_URL + REGISTRATION_ENDPOINT)
                .getBody()
                .path("token_value");
        long id = userProfileService.findUserProfileByEmail(email).getId();
        return new Registration(id, email, pass, token);
    }

    public void doConfirmation(String email, String token) {
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(email, token))
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT);
    }

    public Registration doConfirmedRegistration() {
        var registration = doRegistration();
        var email = registration.getEmail();
        var token = registration.getToken();
        doConfirmation(email, token);
        return registration;
    }

    public String doAuthentication() {
        return doAuthentication(doConfirmedRegistration());
    }

    public String doAuthentication(Registration registration) {
        return doAuthentication(
                registration.getEmail(),
                registration.getPassword());
    }

    public String doAdminAuthentication() {
        var password = "admin";
        var email = savedAdmin(password).getEmail();
        return doAuthentication(email, password);
    }

    public String doAuthentication(String email, String password) {
        return "Bearer " + given()
                .contentType(URLENC)
                .accept(JSON)
                .formParams(
                        "username", email,
                        "password", password,
                        "grant_type", "password",
                        "scope", "access_profile")
                .auth()
                .preemptive()
                .basic(OAUTH2_CLIENT_ID, OAUTH2_SECRET)
                .when()
                .post(API_URL + "/oauth/token")
                .body()
                .jsonPath()
                .getString("access_token");
    }

    public Response doAuthenticationRequest(Registration registration) {
        return given()
                .contentType(URLENC)
                .accept(JSON)
                .formParams(
                        "username", registration.getEmail(),
                        "password", registration.getPassword(),
                        "grant_type", "password",
                        "scope", "access_profile")
                .auth()
                .preemptive()
                .basic(OAUTH2_CLIENT_ID, OAUTH2_SECRET)
                .when()
                .post(API_URL + "/oauth/token");
    }

    public String doForgotPasswordRequest(String email) {
        var request = new ForgotPasswordRequest(email);
        userPasswordFacade.handleForgotPasswordRequest(request);
        var userId = userProfileService.findUserProfileByEmail(email).getId();
        return tokenRepository.findByTypeAndUserId(PASSWORD_RESET, userId).orElseThrow().getValue();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime expired() {
        return LocalDateTime.now().minusHours(1);
    }

    public static LocalDateTime notExpired() {
        return LocalDateTime.now().plusHours(1);
    }

}
