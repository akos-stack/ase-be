package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public class OAuthClientDetailsServiceImplTest extends AbstractSpringTest {

    @Autowired
    private OAuthClientDetailsRepository repository;

    @Autowired
    private OAuthClientDetailsServiceImpl service;

    @Test(expected = NullPointerException.class)
    public void findOAuthClientDetailsByClientId_null() {
        service.findOAuthClientDetailsByClientId(null);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findOAuthClientDetailsByClientId_notFound() {
        var id = UUID.randomUUID().toString();
        service.findOAuthClientDetailsByClientId(id);
    }

    @Test
    public void findOAuthClientDetailsByClientId() {
        var id = UUID.randomUUID().toString();
        var oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setClientId(id);
        repository.save(oAuthClientDetails);
        service.findOAuthClientDetailsByClientId(id);
    }

    @Test(expected = NullPointerException.class)
    public void loadClientByClientId_null() {
        service.loadClientByClientId(null);
    }

    @Test(expected = EntityNotFoundException.class)
    public void loadClientByClientId_notFound() {
        var id = UUID.randomUUID().toString();
        service.loadClientByClientId(id);
    }

    @Test
    public void loadClientByClientId() {
        var id = UUID.randomUUID().toString();
        var oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setClientId(id);
        oAuthClientDetails.setScope("foo,bar,baz");
        oAuthClientDetails.setAuthorizedGrantTypes("foo,bar,baz");
        oAuthClientDetails.setAuthorities("foo,bar,baz");
        repository.save(oAuthClientDetails);
        service.loadClientByClientId(id);
    }

}
