package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Owner;
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
        var creator = mockUtil.savedUserProfile();
        owner.setUserProfile(creator);
        owner.setCreatorId(creator.getId());
        var id = repository.saveAndFlush(owner).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
