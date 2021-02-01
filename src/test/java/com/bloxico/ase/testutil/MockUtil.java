package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.UserProfile;
import com.bloxico.ase.userservice.facade.impl.*;
import com.bloxico.ase.userservice.repository.address.*;
import com.bloxico.ase.userservice.repository.oauth.OAuthAccessTokenRepository;
import com.bloxico.ase.userservice.repository.token.*;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserRepository;
import com.bloxico.ase.userservice.repository.user.profile.UserProfileRepository;
import com.bloxico.ase.userservice.service.token.impl.PendingEvaluatorServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserServiceImpl;
import com.bloxico.ase.userservice.web.model.password.ForgotPasswordRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.EvaluatorInvitationRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
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
import java.util.stream.Collectors;
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
import static java.util.Objects.requireNonNull;
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
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
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
    private final PendingEvaluatorServiceImpl pendingEvaluatorService;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public MockUtil(PasswordEncoder passwordEncoder,
                    RoleRepository roleRepository,
                    UserRepository userRepository,
                    UserServiceImpl userService,
                    UserPasswordFacadeImpl userPasswordFacade,
                    OAuthAccessTokenRepository oAuthAccessTokenRepository,
                    TokenRepository tokenRepository,
                    BlacklistedTokenRepository blacklistedTokenRepository,
                    UserManagementFacadeImpl userManagementFacade,
                    CountryRepository countryRepository,
                    CityRepository cityRepository,
                    LocationRepository locationRepository,
                    UserRegistrationFacadeImpl userRegistrationFacade,
                    PendingEvaluatorRepository pendingEvaluatorRepository,
                    PendingEvaluatorServiceImpl pendingEvaluatorService,
                    UserProfileRepository userProfileRepository)
    {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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
        this.pendingEvaluatorService = pendingEvaluatorService;
        this.userProfileRepository = userProfileRepository;
    }

    public User savedAdmin() {
        return savedAdmin(genEmail());
    }

    public User savedAdmin(String password) {
        return savedAdmin(genEmail(), password);
    }

    public User savedAdmin(String email, String password) {
        var user = new User();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(roleRepository.getAdminRole());
        return userRepository.saveAndFlush(user);
    }

    public User savedUser() {
        return savedUser(genEmail());
    }

    public User savedUser(String password) {
        return savedUser(genEmail(), password);
    }

    public User savedUser(String email, String password) {
        var user = new User();
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(roleRepository.getUserRole());
        return userRepository.saveAndFlush(user);
    }

    public UserDto savedUserDto() {
        return MAPPER.toDto(savedUser());
    }

    public UserProfile savedUserProfile(long userId) {
        var userProfile = new UserProfile();
        userProfile.setUserId(userId);
        userProfile.setFirstName(uuid());
        userProfile.setLastName(uuid());
        userProfile.setBirthday(LocalDate.now().minusYears(20));
        userProfile.setGender(uuid());
        userProfile.setPhone(uuid());
        userProfile.setLocation(savedLocation());
        userProfile.setCreatorId(userId);
        return userProfileRepository.saveAndFlush(userProfile);
    }

    public UserProfile savedUserProfile() {
        return savedUserProfile(savedUser().getId());
    }

    public UserProfileDto savedUserProfileDto(long userId) {
        return MAPPER.toDto(savedUserProfile(userId));
    }

    public UserProfileDto savedUserProfileDto() {
        return savedUserProfileDto(savedUser().getId());
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
        var userId = savedUser().getId();
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
        var user = savedUser();
        var token = savedOauthTokenDto(user.getEmail());
        userManagementFacade.blacklistTokens(user.getId(), adminId);
        return getBlacklistedToken(token.getTokenId());
    }

    public BlacklistedToken savedExpiredBlacklistedToken() {
        var adminId = savedAdmin().getId();
        var user = savedUser();
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

    public OAuthAccessTokenDto toOAuthAccessTokenDto(User user, String token) {
        return toOAuthAccessTokenDto(user.getEmail(), token);
    }

    public OAuthAccessTokenDto toOAuthAccessTokenDto(String email, String token) {
        return new OAuthAccessTokenDto(
                token.contains("Bearer ") ? token.replace("Bearer ", "") : token,
                null, null,
                email,
                "appId",
                null,
                notExpired(),
                uuid());
    }

    public void disableUser(Long userId) {
        var user = userRepository
                .findById(userId)
                .orElseThrow();
        assertTrue(user.getEnabled());
        user.setEnabled(false);
        user.setUpdaterId(user.getId());
        userRepository.saveAndFlush(user);
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

    public SubmitArtOwnerRequest newSubmitArtOwnerRequest() {
        return new SubmitArtOwnerRequest(
                uuid(), genEmail(),
                genEmail(), uuid(), uuid(),
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

    public RegistrationRequest genRegistrationRequest() {
        var email = genEmail();
        return new RegistrationRequest(email, email, email, email);
    }

    public UserDto genUserDto() {
        var email = genEmail();
        var userDto = new UserDto();
        userDto.setName(email);
        userDto.setPassword(email);
        userDto.setEmail(email);
        return userDto;
    }

    @lombok.Value
    public static class Registration {
        long id;
        String email, username, password, token;
    }

    public Registration doRegistration() {
        String email = genEmail(), pass = genEmail();
        String token = given()
                .contentType(JSON)
                .body(new RegistrationRequest(email, email, pass, pass))
                .post(API_URL + REGISTRATION_ENDPOINT)
                .getBody()
                .path("token_value");
        long id = userService.findUserByEmail(email).getId();
        return new Registration(id, email, email, pass, token);
    }

    public void doConfirmation(String token) {
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(token))
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT);
    }

    public Registration doConfirmedRegistration() {
        var registration = doRegistration();
        var token = registration.getToken();
        doConfirmation(token);
        return registration;
    }

    public String doAuthentication() {
        return doAuthentication(doConfirmedRegistration());
    }

    public String doAuthentication(String password) {
        var email = savedUser(password).getEmail();
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
        return "Bearer " + requireNonNull(given()
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
                .getString("access_token"));
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
        var userId = userService.findUserByEmail(email).getId();
        return tokenRepository
                .findByTypeAndUserId(PASSWORD_RESET, userId)
                .orElseThrow()
                .getValue();
    }

    public void saveUsers() {
        savedAdmin("user1@gmail.com", "123!");
        savedAdmin("user2@gmail.com", "123!");
        savedAdmin("user3@gmail.com", "123!");
    }

    public boolean isEvaluatorAlreadyPending(String email) {
        return pendingEvaluatorRepository
                .findByEmailIgnoreCase(email)
                .isPresent();
    }

    public List<PendingEvaluatorDto> createInvitedPendingEvaluators() {
        var emails = Stream
                .generate(MockUtil::genEmail)
                .limit(10)
                .collect(Collectors.toList());

        return createInvitedPendingEvaluators(emails);
    }

    public List<PendingEvaluatorDto> createInvitedPendingEvaluators(List<String> emails) {
        var adminId = savedAdmin().getId();

        return emails
                .stream()
                .map(EvaluatorInvitationRequest::new)
                .map(request -> pendingEvaluatorService.createPendingEvaluator(request, adminId))
                .collect(Collectors.toList());
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
