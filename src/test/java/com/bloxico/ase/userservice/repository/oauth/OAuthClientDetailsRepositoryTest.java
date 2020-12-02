package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.oauth.OAuthClientDetails;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class OAuthClientDetailsRepositoryTest extends AbstractSpringTest {

    @Autowired
    private OAuthClientDetailsRepository repository;

    @Test
    public void save() {
        var id = UUID.randomUUID().toString();
        var details = new OAuthClientDetails();
        details.setClientId(id);
        repository.save(details);
    }

    @Test
    public void findByClientId_nullClientId() {
        assertTrue(repository.findByClientId(null).isEmpty());
        var details = new OAuthClientDetails();
        details.setClientId(UUID.randomUUID().toString());
        repository.save(details);
        assertTrue(repository.findByClientId(null).isEmpty());
    }

    @Test
    public void findByClientId() {
        var id = UUID.randomUUID().toString();
        assertTrue(repository.findByClientId(id).isEmpty());
        var details = new OAuthClientDetails();
        details.setClientId(id);
        repository.save(details);
        assertTrue(repository.findByClientId(id).isPresent());
    }

}
