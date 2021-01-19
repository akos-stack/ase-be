package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlacklistedTokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private BlacklistedTokenRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var token = new BlacklistedToken();
        token.setValue(uuid());
        token.setExpiryDate(LocalDateTime.now());
        token.setCreatorId(mockUtil.savedUser().getId());
        var id = repository.saveAndFlush(token).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findDistinctTokenValues() {
        assertTrue(repository.findDistinctTokenValues().isEmpty());
        var creatorId = mockUtil.savedUser().getId();
        var size = 5;
        var tokens = Stream
                .generate(BlacklistedToken::new)
                .peek(t -> t.setValue(uuid()))
                .peek(t -> t.setExpiryDate(LocalDateTime.now()))
                .peek(t -> t.setCreatorId(creatorId))
                .limit(size)
                .collect(toSet());
        var inMemorySet = repository
                .saveAll(tokens)
                .stream()
                .map(BlacklistedToken::getValue)
                .collect(toSet());
        var inDatabase = repository.findDistinctTokenValues();
        var inDatabaseSet = Set.copyOf(inDatabase);
        assertEquals(size, inDatabaseSet.size());
        assertEquals(inDatabaseSet.size(), inDatabase.size());
        assertEquals(inMemorySet, inDatabaseSet);
    }

    @Test
    public void deleteExpiredTokens() {
        var valid = mockUtil.savedBlacklistedToken();
        var expired = mockUtil.savedExpiredBlacklistedToken();
        assertEquals(
                Set.of(valid, expired),
                Set.copyOf(repository.findAll()));
        repository.deleteExpiredTokens();
        assertEquals(
                Set.of(valid),
                Set.copyOf(repository.findAll()));
    }

}
