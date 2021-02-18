package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AseSecurityServiceTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private AseSecurityService service;
    @Autowired private OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Test
    public void loadUserByUsername_nullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> service.loadUserByUsername(null));
    }

    @Test
    public void loadUserByUsername_notFound() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(genUUID()));
    }

    @Test
    public void loadUserByUsername() {
        var user = utilUser.savedUser();
        assertEquals(
                AsePrincipal.newUserDetails(user),
                service.loadUserByUsername(user.getEmail()));
    }

    @Test
    public void loadUser_null() {
        assertThrows(
                NullPointerException.class,
                () -> service.loadUser(null));
    }

    // TODO-TEST loadUser_notExists (for each provider)
    @Test
    public void loadUser_notExists() {
    }

    // TODO-TEST loadUser_exists (for each provider)
    @Test
    public void loadUser_exists() {
    }

    @Test
    public void loadClientByClientId_null() {
        assertThrows(
                NullPointerException.class,
                () -> service.loadClientByClientId(null));
    }

    @Test
    public void loadClientByClientId_notFound() {
        assertThrows(
                ClientRegistrationException.class,
                () -> service.loadClientByClientId(genUUID()));
    }

    @Test
    public void loadClientByClientId() {
        var id = genUUID();
        var oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setClientId(id);
        oAuthClientDetails.setScope("foo,bar,baz");
        oAuthClientDetails.setAuthorizedGrantTypes("foo,bar,baz");
        oAuthClientDetails.setAuthorities("foo,bar,baz");
        oAuthClientDetailsRepository.save(oAuthClientDetails);
        var clientDetails = service.loadClientByClientId(id);
        assertEquals(
                oAuthClientDetails.getClientId(),
                clientDetails.getClientId());
        assertEquals(
                Set.of(oAuthClientDetails.getScope().split(",")),
                clientDetails.getScope());
        assertEquals(
                Set.of(oAuthClientDetails.getAuthorizedGrantTypes().split(",")),
                clientDetails.getAuthorizedGrantTypes());
        assertEquals(
                oAuthClientDetails.getClientId(),
                clientDetails.getClientId());
    }

}
