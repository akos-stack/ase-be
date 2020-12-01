package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class OAuthAccessTokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private OAuthAccessTokenRepository repository;

    @Test
    public void save() {
        var token = new OAuthAccessToken();
        token.setUserName("fooBar@mail.com");
        token.setTokenId(UUID.randomUUID().toString());
        repository.save(token);
    }

    @Test
    public void findById() {
        var id = UUID.randomUUID().toString();
        assertTrue(repository.findById(id).isEmpty());
        var token = new OAuthAccessToken();
        token.setUserName("fooBar@mail.com");
        token.setTokenId(id);
        repository.save(token);
        assertTrue(repository.findById(token.getTokenId()).isPresent());
    }

}
