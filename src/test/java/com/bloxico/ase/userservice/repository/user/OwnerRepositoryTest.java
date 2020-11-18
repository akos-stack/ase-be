package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Owner;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OwnerRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OwnerRepository repository;

    @Test
    public void save() {
        Owner user = new Owner();
        UserProfile creator = mockUtil.savedUserProfile();
        user.setUserProfile(creator);
        user.setBirthday(LocalDate.now());
        user.setCreatorId(creator.getId());
        user = repository.saveAndFlush(user);
        assertNotNull(user.getId());
    }

    @Test
    public void findById() {
        assertTrue(repository.findById(-1L).isEmpty());
        Owner user = new Owner();
        UserProfile creator = mockUtil.savedUserProfile();
        user.setUserProfile(creator);
        user.setBirthday(LocalDate.now());
        user.setCreatorId(creator.getId());
        user = repository.saveAndFlush(user);
        assertTrue(repository.findById(user.getId()).isPresent());
    }

}
