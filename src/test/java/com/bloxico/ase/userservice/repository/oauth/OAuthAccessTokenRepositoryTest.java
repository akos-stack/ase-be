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
    public void findAllByUserNameIgnoreCase_nullEmail() {
        assertTrue(repository.findAllByUserNameIgnoreCase(null).isEmpty());
        mockUtil.genSavedTokens(5, "fooBar@mail.com");
        assertTrue(repository.findAllByUserNameIgnoreCase(null).isEmpty());
    }

    @Test
    public void findAllByUserNameIgnoreCase() {
        var email = "fooBar@mail.com";
        assertTrue(repository.findAllByUserNameIgnoreCase(email).isEmpty());
        var size = 5;
        var tokens = mockUtil.genSavedTokens(size, email.toUpperCase());
        repository.saveAll(tokens);
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email).size());
    }

    @Test
    public void deleteByUserNameIgnoreCase_nullEmail() {
        var email = "fooBar@mail.com";
        assertTrue(repository.findAllByUserNameIgnoreCase(email).isEmpty());
        var size = 5;
        mockUtil.genSavedTokens(size, email);
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email).size());
        repository.deleteByUserNameIgnoreCase(null);
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email).size());
    }

    @Test
    public void deleteByUserNameIgnoreCase() {
        var email1 = "fooBar@mail.com";
        var email2 = "barFoo@mail.com";
        var size = 5;
        mockUtil.genSavedTokens(size, email1.toUpperCase());
        mockUtil.genSavedTokens(size, email2);
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email1).size());
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email2).size());
        repository.deleteByUserNameIgnoreCase(email1);
        assertEquals(0, repository.findAllByUserNameIgnoreCase(email1).size());
        assertEquals(size, repository.findAllByUserNameIgnoreCase(email2).size());
    }

}
