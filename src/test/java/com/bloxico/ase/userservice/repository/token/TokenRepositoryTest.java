package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.Token;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.randOtherEnumConst;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class TokenRepositoryTest extends AbstractSpringTest {

    protected abstract Token.Type tokenType();

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
        token.setType(tokenType());
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setCreatorId(admin.getId());
        var id = repository.saveAndFlush(token).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByValue() {
        var value = uuid();
        assertTrue(repository.findByValue(value).isEmpty());
        mockUtil.savedToken(tokenType(), value);
        assertTrue(repository.findByValue(value).isPresent());
    }

    @Test
    public void findByTypeAndUserId() {
        var type = tokenType();
        var otherType = randOtherEnumConst(type);
        var token = mockUtil.savedToken(type);
        assertTrue(repository.findByTypeAndUserId(type, token.getUserId()).isPresent());
        assertTrue(repository.findByTypeAndUserId(otherType, token.getUserId()).isEmpty());
    }

    @Test
    public void findAllExpiredTokensByType() {
        var type = tokenType();
        var otherType = randOtherEnumConst(type);
        mockUtil.savedToken(type);
        mockUtil.savedExpiredToken(otherType);
        var expired = mockUtil.savedExpiredToken(type);
        assertEquals(
                List.of(expired),
                repository.findAllExpiredTokensByType(type));
    }

}
