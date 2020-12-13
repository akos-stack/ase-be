package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.Token;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.token.Token.Type.PASSWORD_RESET;
import static com.bloxico.ase.userservice.entity.token.Token.Type.REGISTRATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private TokenRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var admin = mockUtil.savedAdmin();
        var token = new Token();
        token.setUserId(admin.getId());
        token.setValue(uuid());
        token.setType(REGISTRATION);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setCreatorId(admin.getId());
        var id = repository.saveAndFlush(token).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByValue() {
        var value = uuid();
        assertTrue(repository.findByValue(value).isEmpty());
        mockUtil.savedToken(PASSWORD_RESET, value);
        assertTrue(repository.findByValue(value).isPresent());
    }

    @Test
    public void findByTypeAndUserId() {
        var token = mockUtil.savedToken(REGISTRATION);
        assertTrue(repository.findByTypeAndUserId(PASSWORD_RESET, token.getUserId()).isEmpty());
        assertTrue(repository.findByTypeAndUserId(REGISTRATION, token.getUserId()).isPresent());
    }

    @Test
    public void deleteExpiredTokens() {
        var valid = mockUtil.savedToken(REGISTRATION);
        var expired = mockUtil.savedExpiredToken(PASSWORD_RESET);
        assertEquals(
                Set.of(valid, expired),
                Set.copyOf(repository.findAll()));
        repository.deleteExpiredTokens();
        assertEquals(
                Set.of(valid),
                Set.copyOf(repository.findAll()));
    }

}
