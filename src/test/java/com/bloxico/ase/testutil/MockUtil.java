package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.address.City;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.entity.address.Location;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.facade.impl.UserManagementFacadeImpl;
import com.bloxico.ase.userservice.facade.impl.UserPasswordFacadeImpl;
import com.bloxico.ase.userservice.facade.impl.UserRegistrationFacadeImpl;
import com.bloxico.ase.userservice.repository.address.CityRepository;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.BlacklistedTokenRepository;
import com.bloxico.ase.userservice.repository.token.PendingEvaluatorRepository;
import com.bloxico.ase.userservice.repository.token.TokenRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_CONFIRM_ENDPOINT;
import static com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_ENDPOINT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.function.Predicate.not;
import static org.junit.Assert.assertTrue;

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
    private final UserManagementFacadeImpl userManagementFacade;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final UserRegistrationFacadeImpl userRegistrationFacade;
    private final PendingEvaluatorRepository pendingEvaluatorRepository;

    @Autowired
    public MockUtil(PasswordEncoder passwordEncoder,
                    RoleRepository roleRepository,
                    UserProfileRepository userProfileRepository,
                    UserProfileServiceImpl userProfileService,
                    UserPasswordFacadeImpl userPasswordFacade,
                    OAuthAccessTokenRepository oAuthAccessTokenRepository,
                    TokenRepository tokenRepository,
                    BlacklistedTokenRepository blacklistedTokenRepository,
                    UserManagementFacadeImpl userManagementFacade,
                    CountryRepository countryRepository,
                    CityRepository cityRepository,
                    LocationRepository locationRepository,
                    UserRegistrationFacadeImpl userRegistrationFacade,
                    PendingEvaluatorRepository pendingEvaluatorRepository)
    {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileService = userProfileService;
        this.userPasswordFacade = userPasswordFacade;
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.tokenRepository = tokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.userManagementFacade = userManagementFacade;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.userRegistrationFacade = userRegistrationFacade;
        this.pendingEvaluatorRepository = pendingEvaluatorRepository;
    }

    public UserProfile savedAdmin() {
        return savedAdmin(genEmail());
    }

    public UserProfile savedAdmin(String password) {
        return savedAdmin(genEmail(), password);
    }

    public UserProfile savedAdmin(String email, String password) {
        var user = new UserProfile();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(roleRepository.getAdminRole());
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfile savedUserProfile() {
        return savedUserProfile(genEmail());
    }

    public UserProfile savedUserProfile(String password) {
        return savedUserProfile(genEmail(), password);
    }

    public UserProfile savedUserProfile(String email, String password) {
        var user = new UserProfile();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(roleRepository.getUserRole());
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfileDto savedUserProfileDto() {
        return MAPPER.toDto(savedUserProfile());
    }

    public Country savedCountry() {
        var creatorId = savedAdmin().getId();
        var country = new Country();
        country.setName(uuid());
        country.setCreatorId(creatorId);
        return countryRepository.saveAndFlush(country);
    }

    public CountryDto savedCountryDto() {
        return MAPPER.toDto(savedCountry());
    }

    public City savedCity() {
        var country = savedCountry();
        var city = new City();
        city.setCountry(country);
        city.setName(uuid());
        city.setZipCode(uuid());
        city.setCreatorId(country.getCreatorId());
        return cityRepository.saveAndFlush(city);
    }

    public CityDto savedCityDto() {
        return MAPPER.toDto(savedCity());
    }

    public Location savedLocation() {
        var city = savedCity();
        var location = new Location();
        location.setCity(city);
        location.setAddress(uuid());
        location.setCreatorId(city.getCreatorId());
        return locationRepository.saveAndFlush(location);
    }

    public LocationDto savedLocationDto() {
        return MAPPER.toDto(savedLocation());
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
                .filter(t -> t.getValue().equals(value))
                .findAny()
                .orElseThrow();
    }

    public BlacklistedToken savedBlacklistedToken() {
        var adminId = savedAdmin().getId();
        var user = savedUserProfile();
        var token = savedOauthTokenDto(user.getEmail());
        userManagementFacade.blacklistTokens(user.getId(), adminId);
        return getBlacklistedToken(token.getTokenId());
    }

    public BlacklistedToken savedExpiredBlacklistedToken() {
        var adminId = savedAdmin().getId();
        var user = savedUserProfile();
        var token = savedExpiredOauthTokenDto(user.getEmail());
        userManagementFacade.blacklistTokens(user.getId(), adminId);
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

    public void disableUser(Long userId) {
        var user = userProfileRepository
                .findById(userId)
                .orElseThrow();
        assertTrue(user.getEnabled());
        user.setEnabled(false);
        user.setUpdaterId(user.getId());
        userProfileRepository.saveAndFlush(user);
    }

    public SubmitEvaluatorRequest newSubmitUninvitedEvaluatorRequest() {
        var email = genEmail();
        var password = genEmail();
        return new SubmitEvaluatorRequest(
                uuid(), uuid(), password,
                email, uuid(), uuid(),
                uuid(), LocalDate.now(),
                uuid(), uuid(), uuid(),
                uuid(), uuid(), ONE, TEN);
    }

    public SubmitEvaluatorRequest newSubmitInvitedEvaluatorRequest() {
        var email = genEmail();
        var password = genEmail();
        var principalId = savedAdmin().getId();
        userRegistrationFacade.sendEvaluatorInvitation(new EvaluatorInvitationRequest(email), principalId);
        var token = pendingEvaluatorRepository.findByEmailIgnoreCase(email).orElseThrow().getToken();
        return new SubmitEvaluatorRequest(
                token, uuid(), password,
                email, uuid(), uuid(),
                uuid(), LocalDate.now(),
                uuid(), uuid(), uuid(),
                uuid(), uuid(), ONE, TEN);
    }

    @lombok.Value
    public static class Registration {
        long id;
        String email, password, token;
    }

    public Registration doRegistration() {
        String email = genEmail(), pass = genEmail();
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

    public String doAuthentication(String password) {
        var email = savedUserProfile(password).getEmail();
        return doAuthentication(email, password);
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

    public void saveUserProfiles() {
        savedUserProfile("user1@gmail.com", "123!");
        savedUserProfile("user2@gmail.com", "123!");
        savedUserProfile("user3@gmail.com", "123!");
    }

    public boolean evaluatorAlreadyPending(String email) {
        return pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .isPresent();
    }

    private static final AtomicLong along = new AtomicLong(0);

    public static String genEmail() {
        return along.incrementAndGet() + "aseUser@mail.com";
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

    public static <T> T randElt(List<? extends T> list) {
        return list.get(current().nextInt(0, list.size()));
    }

    public static <T extends Enum<T>> T randEnumConst(Class<T> type) {
        return randElt(List.of(type.getEnumConstants()));
    }

    public static <T extends Enum<T>> T randOtherEnumConst(T eConst) {
        return Stream
                .generate(() -> randEnumConst(eConst.getDeclaringClass()))
                .filter(not(eConst::equals))
                .findAny()
                .orElseThrow();
    }

}
