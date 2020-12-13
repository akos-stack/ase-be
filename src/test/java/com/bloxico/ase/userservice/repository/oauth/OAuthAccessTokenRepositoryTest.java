package com.bloxico.ase.userservice.repository.oauth;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OAuthAccessTokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OAuthAccessTokenRepository repository;

    @Test
    public void saveAndFindById() {
        var id = uuid();
        assertTrue(repository.findById(id).isEmpty());
        var token = new OAuthAccessToken();
        token.setUserName("fooBar@mail.com");
        token.setTokenId(id);
        repository.save(token);
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findAllByUserNameIgnoreCase() {
        var email = "fooBar@mail.com";
        assertTrue(repository.findAllByUserNameIgnoreCase(email).isEmpty());
        var token1 = mockUtil.savedOauthTokenDto(email);
        var token2 = mockUtil.savedOauthTokenDto(email.toUpperCase());
        assertEquals(
                Set.copyOf(repository.findAllByUserNameIgnoreCase(email)),
                Set.of(token1, token2));
    }

    @Test
    public void deleteByUserNameIgnoreCase() {
        var email1 = "fooBar@mail.com";
        var email2 = "barFoo@mail.com";
        var token1 = mockUtil.savedOauthTokenDto(email1.toUpperCase());
        var token2 = mockUtil.savedOauthTokenDto(email2);
        assertEquals(List.of(token1), repository.findAllByUserNameIgnoreCase(email1));
        assertEquals(List.of(token2), repository.findAllByUserNameIgnoreCase(email2));
        repository.deleteByUserNameIgnoreCase(email1);
        assertEquals(List.of(), repository.findAllByUserNameIgnoreCase(email1));
        assertEquals(List.of(token2), repository.findAllByUserNameIgnoreCase(email2));
    }

    @Test
    public void deleteExpiredTokens() {
        var email = uuid();
        var valid = mockUtil.savedOauthTokenDto(email);
        var expired = mockUtil.savedExpiredOauthTokenDto(email);
        assertEquals(
                Set.of(valid, expired),
                Set.copyOf(repository.findAllByUserNameIgnoreCase(email)));
        repository.deleteExpiredTokens();
        assertEquals(
                Set.of(valid),
                Set.copyOf(repository.findAllByUserNameIgnoreCase(email)));
    }

}
