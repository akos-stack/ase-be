package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserProfileRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserProfileRepository repository;

    @Test
    public void saveAndFindById() {
        assertFalse(repository.findById(-1L).isPresent());
        var id = mockUtil.savedUserProfile().getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByEmailIgnoreCase() {
        assertTrue(repository.findByEmailIgnoreCase(uuid()).isEmpty());
        UserProfile user = mockUtil.savedUserProfile();
        assertTrue(repository.findByEmailIgnoreCase(user.getEmail()).isPresent());
    }

}
