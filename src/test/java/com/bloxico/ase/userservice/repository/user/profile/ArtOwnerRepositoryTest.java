package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class ArtOwnerRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private ArtOwnerRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var artOwner = new ArtOwner();
        var userProfile = mockUtil.savedUserProfile();
        artOwner.setUserProfile(userProfile);
        artOwner.setCreatorId(userProfile.getUserId());
        var id = repository.saveAndFlush(artOwner).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
