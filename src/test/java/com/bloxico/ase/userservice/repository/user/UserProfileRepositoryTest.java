package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void findAllDisabledByIds() {
        var up1 = mockUtil.savedUserProfile();
        var up2 = mockUtil.savedUserProfile();
        var up3 = mockUtil.savedUserProfile();
        var ids = List.of(up1.getId(), up2.getId(), up3.getId());
        assertEquals(
                List.of(),
                repository.findAllDisabledByIds(ids));
        mockUtil.disableUser(up1.getId());
        mockUtil.disableUser(up3.getId());
        assertEquals(
                Set.of(up1, up3),
                Set.copyOf(repository.findAllDisabledByIds(ids)));
    }

}
