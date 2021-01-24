package com.bloxico.ase.userservice.repository.aspiration;

import com.bloxico.ase.testutil.AbstractSpringTest;
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
    private AspirationRepository aspirationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void saveAndFindById() {
        assertTrue(aspirationRepository.findById((short) -1).isEmpty());

        var aspiration = new Aspiration();
        aspiration.setRole(roleRepository.getUserRole());
        var id = aspirationRepository.saveAndFlush(aspiration).getId();

        assertTrue(aspirationRepository.findById(id).isPresent());
    }

    @Test
    public void findByRoleName() {
        assertFalse(aspirationRepository.findByRoleName(uuid()).isPresent());
        assertTrue(aspirationRepository.findByRoleName("evaluator").isPresent());
    }

    @Test
    public void findByRoleNameIn() {
        var shouldFindZero = aspirationRepository.findAllByRoleNameIn(Arrays.asList(uuid(), uuid()));
        assertEquals(0, shouldFindZero.size());

        var shouldFindOne = aspirationRepository.findAllByRoleNameIn(Arrays.asList("evaluator", uuid()));
        assertEquals(1, shouldFindOne.size());
    }

    @Test
    public void getEvaluatorAspiration() {
        assertEquals(Role.EVALUATOR, aspirationRepository.getEvaluatorAspiration().getRole().getName());
    }

}
