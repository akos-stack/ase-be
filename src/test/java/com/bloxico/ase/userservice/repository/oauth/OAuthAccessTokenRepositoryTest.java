package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OAuthAccessTokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

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

    @Test
    public void findAllByUserName_nullEmail() {
        assertTrue(repository.findAllByUserName(null).isEmpty());
        mockUtil.genSavedTokens(5, "fooBar@mail.com");
        assertTrue(repository.findAllByUserName(null).isEmpty());
    }

    @Test
    public void findAllByUserName() {
        var email = "fooBar@mail.com";
        assertTrue(repository.findAllByUserName(email).isEmpty());
        var size = 5;
        var tokens = mockUtil.genSavedTokens(size, email);
        repository.saveAll(tokens);
        assertEquals(size, repository.findAllByUserName(email).size());
    }

    @Test
    public void deleteByUserName_nullEmail() {
        var email = "fooBar@mail.com";
        assertTrue(repository.findAllByUserName(email).isEmpty());
        var size = 5;
        mockUtil.genSavedTokens(size, email);
        assertEquals(size, repository.findAllByUserName(email).size());
        repository.deleteByUserName(null);
        assertEquals(size, repository.findAllByUserName(email).size());
    }

    @Test
    public void deleteByUserName() {
        var email1 = "fooBar@mail.com";
        var email2 = "barFoo@mail.com";
        var size = 5;
        mockUtil.genSavedTokens(size, email1);
        mockUtil.genSavedTokens(size, email2);
        assertEquals(size, repository.findAllByUserName(email1).size());
        assertEquals(size, repository.findAllByUserName(email2).size());
        repository.deleteByUserName(email1);
        assertEquals(0, repository.findAllByUserName(email1).size());
        assertEquals(size, repository.findAllByUserName(email2).size());
    }

}
