package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.config.security.AsePrincipal.newUserDetails;
import static org.junit.Assert.assertEquals;

public class AseSecurityServiceTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private AseSecurityService service;

    @Autowired
    private OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Test(expected = NullPointerException.class)
    public void loadUserByUsername_nullEmail() {
        service.loadUserByUsername(null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_notFound() {
        service.loadUserByUsername(uuid());
    }

    @Test
    public void loadUserByUsername_found() {
        var user = mockUtil.savedUser();
        assertEquals(
                newUserDetails(user),
                service.loadUserByUsername(user.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void loadClientByClientId_null() {
        service.loadClientByClientId(null);
    }

    @Test(expected = ClientRegistrationException.class)
    public void loadClientByClientId_notFound() {
        var id = uuid();
        service.loadClientByClientId(id);
    }

    @Test
    public void loadClientByClientId() {
        var id = uuid();
        var oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setClientId(id);
        oAuthClientDetails.setScope("foo,bar,baz");
        oAuthClientDetails.setAuthorizedGrantTypes("foo,bar,baz");
        oAuthClientDetails.setAuthorities("foo,bar,baz");
        oAuthClientDetailsRepository.save(oAuthClientDetails);
        service.loadClientByClientId(id);
    }

}
