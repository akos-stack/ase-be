package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.user.Role.ADMIN;
import static org.junit.Assert.*;

public class UserRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private UserRepository repository;

    @Test
    public void saveAndFindById() {
        assertFalse(repository.findById(-1L).isPresent());
        var id = mockUtil.savedUser().getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByEmailIgnoreCase() {
        assertTrue(repository.findByEmailIgnoreCase(uuid()).isEmpty());
        var user = mockUtil.savedUser();
        assertTrue(repository.findByEmailIgnoreCase(user.getEmail()).isPresent());
    }

    @Test
    public void findAllDisabledByIds() {
        var up1 = mockUtil.savedUser();
        var up2 = mockUtil.savedUser();
        var up3 = mockUtil.savedUser();
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

    @Test
    public void findByEmailAndRole() {
        mockUtil.saveUsers();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        assertEquals(4, repository.findDistinctByEmailContainingAndRoles_NameContaining("", ADMIN, pageable).getContent().size());
    }

}
