package com.bloxico.ase.userservice.repository.aspiration;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.aspiration.Aspiration;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.repository.user.RoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.*;

public class AspirationRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private AspirationRepository aspirationRepository;

    @Test
    public void saveAndFindById() {
        assertTrue(aspirationRepository.findById((short) -1).isEmpty());
        var aspiration = mockUtil.savedUserAspiration();
        assertTrue(aspirationRepository.findById(aspiration.getId()).isPresent());
    }

    @Test
    public void findByRoleName() {
        assertFalse(aspirationRepository.findByRoleName(uuid()).isPresent());
        var aspiration = mockUtil.savedUserAspiration();
        assertTrue(aspirationRepository.findByRoleName(aspiration.getRole().getName()).isPresent());
    }

    @Test
    public void findByRoleNameIn() {
        var shouldFindZero = aspirationRepository.findAllByRoleNameIn(Arrays.asList(uuid(), uuid()));
        assertEquals(0, shouldFindZero.size());

        var aspiration = mockUtil.savedUserAspiration();

        var shouldFindOne = aspirationRepository
                .findAllByRoleNameIn(Arrays.asList(aspiration.getRole().getName(), uuid()));
        assertEquals(1, shouldFindOne.size());
    }

}
