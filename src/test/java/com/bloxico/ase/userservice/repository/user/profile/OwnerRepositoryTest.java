package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.profile.Owner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class OwnerRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private OwnerRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var owner = new Owner();
        var userProfile = mockUtil.savedUserProfile();
        owner.setUserProfile(userProfile);
        owner.setCreatorId(userProfile.getUserId());
        var id = repository.saveAndFlush(owner).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
