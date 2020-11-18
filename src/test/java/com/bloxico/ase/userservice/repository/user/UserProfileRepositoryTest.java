package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserProfileRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileRepository repository;

    @Test
    public void save() {
        assertNotNull(mockUtil.savedUserProfile().getId());
    }

    @Test
    public void findById() {
        assertFalse(repository.findById(-1L).isPresent());
        UserProfile user = mockUtil.savedUserProfile();
        assertTrue(repository.findById(user.getId()).isPresent());
    }

    @Test
    public void findByEmailIgnoreCase() {
        assertTrue(repository.findByEmailIgnoreCase(UUID.randomUUID().toString()).isEmpty());
        UserProfile user = mockUtil.savedUserProfile();
        assertTrue(repository.findByEmailIgnoreCase(user.getEmail()).isPresent());
    }

}
