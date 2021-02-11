package com.bloxico.ase.userservice.service.oauth.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import com.bloxico.ase.userservice.repository.oauth.OAuthClientDetailsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OAuthClientDetailsServiceImplTest extends AbstractSpringTest {

    @Autowired private OAuthClientDetailsRepository repository;
    @Autowired private OAuthClientDetailsServiceImpl service;

    @Test
    public void findOAuthClientDetailsByClientId_null() {
        assertThrows(
                NullPointerException.class,
                () -> service.findOAuthClientDetailsByClientId(null));
    }

    @Test
    public void findOAuthClientDetailsByClientId_notFound() {
        var id = genUUID();
        assertThrows(
                EntityNotFoundException.class,
                () -> service.findOAuthClientDetailsByClientId(id));
    }

    @Test
    public void findOAuthClientDetailsByClientId() {
        var id = genUUID();
        var oAuthClientDetails = new OAuthClientDetails();
        oAuthClientDetails.setClientId(id);
        repository.save(oAuthClientDetails);
        assertNotNull(service.findOAuthClientDetailsByClientId(id));
    }

}
