package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlacklistedTokenRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private BlacklistedTokenRepository repository;

    @Test
    public void save() {
        BlacklistedToken jwt = new BlacklistedToken();
        jwt.setToken(UUID.randomUUID().toString());
        jwt.setExpiration(LocalDateTime.now());
        jwt.setCreatorId(mockUtil.savedUserProfile().getId());
        repository.saveAndFlush(jwt);
        assertNotNull(jwt.getId());
    }

    @Test
    public void findById() {
        assertTrue(repository.findById(-1L).isEmpty());
        BlacklistedToken jwt = new BlacklistedToken();
        jwt.setToken(UUID.randomUUID().toString());
        jwt.setExpiration(LocalDateTime.now());
        jwt.setCreatorId(mockUtil.savedUserProfile().getId());
        jwt = repository.saveAndFlush(jwt);
        assertTrue(repository.findById(jwt.getId()).isPresent());
    }

    @Test
    public void findDistinctTokenValues() {
        assertTrue(repository.findDistinctTokenValues().isEmpty());
        var creatorId = mockUtil.savedUserProfile().getId();
        var size = 5;
        var tokens = Stream
                .generate(BlacklistedToken::new)
                .peek(t -> t.setToken(UUID.randomUUID().toString()))
                .peek(t -> t.setExpiration(LocalDateTime.now()))
                .peek(t -> t.setCreatorId(creatorId))
                .limit(size)
                .collect(toSet());
        var inMemorySet = repository
                .saveAll(tokens)
                .stream()
                .map(BlacklistedToken::getToken)
                .collect(toSet());
        var inDatabase = repository.findDistinctTokenValues();
        var inDatabaseSet = Set.copyOf(inDatabase);
        assertEquals(size, inDatabaseSet.size());
        assertEquals(inDatabaseSet.size(), inDatabase.size());
        assertEquals(inMemorySet, inDatabaseSet);
    }

}