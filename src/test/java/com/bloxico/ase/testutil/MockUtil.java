package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.TokenValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile savedAdmin() {
        Role role = new Role();
        role.setName("admin");
        roleRepository.save(role);
        UserProfile user = new UserProfile();
        user.setName("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@mail.com");
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(role);
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfile savedUserProfile() {
        Role role = new Role();
        {
            Permission p1 = new Permission();
            p1.setName("permission_1");
            p1 = permissionRepository.saveAndFlush(p1);
            role.addPermission(p1);
            Permission p2 = new Permission();
            p2.setName("permission_2");
            p2 = permissionRepository.saveAndFlush(p2);
            role.setName("role_x");
            role.addPermission(p2);
            roleRepository.saveAndFlush(role);
        }
        UserProfile user = new UserProfile();
        user.setName("foobar");
        user.setPassword(passwordEncoder.encode("foobar"));
        user.setEmail("foobar@mail.com");
        user.setLocked(false);
        user.setEnabled(true);
        user.addRole(role);
        return userProfileRepository.saveAndFlush(user);
    }

    public UserProfileDto savedUserProfileDto() {
        return MAPPER.toUserProfileDto(savedUserProfile());
    }

    public static void copyBaseEntityData(BaseEntity from, BaseEntity to) {
        to.setCreatorId(from.getCreatorId());
        to.setUpdaterId(from.getUpdaterId());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());
    }

    @lombok.Value
    public static class Registration {
        String email, password, token;
    }

    public Registration doRegistration() {
        String email = "passwordMatches@mail.com", pass = "Password1!";
        return new Registration(
                email, pass,
                given()
                        .contentType(JSON)
                        .body(new RegistrationRequest(email, pass, pass))
                        .post(API_URL + REGISTRATION_ENDPOINT)
                        .getBody()
                        .path("token_value"));
    }

    public void doConfirmation(String email, String token) {
        given()
                .contentType(JSON)
                .body(new TokenValidationRequest(email, token))
                .post(API_URL + REGISTRATION_CONFIRM_ENDPOINT);
    }

    public String doAuthentication() {
        var registration = doRegistration();
        var email = registration.getEmail();
        var token = registration.getToken();
        doConfirmation(email, token);
        return "Bearer " + given()
                .contentType(URLENC)
                .accept(JSON)
                .formParams(
                        "username", email,
                        "password", registration.getPassword(),
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

}
