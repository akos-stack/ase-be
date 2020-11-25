package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Evaluator;
import com.bloxico.ase.userservice.entity.user.UserProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EvaluatorRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private EvaluatorRepository repository;

    @Test
    public void save() {
        Evaluator user = new Evaluator();
        UserProfile creator = mockUtil.savedUserProfile();
        user.setUserProfile(creator);
        user.setVerifier(mockUtil.savedAdmin());
        user.setCreatorId(creator.getId());
        user = repository.saveAndFlush(user);
        assertNotNull(user.getId());
    }

    @Test
    public void findById() {
        assertTrue(repository.findById(-1L).isEmpty());
        Evaluator user = new Evaluator();
        UserProfile creator = mockUtil.savedUserProfile();
        user.setUserProfile(creator);
        user.setVerifier(mockUtil.savedAdmin());
        user.setCreatorId(creator.getId());
        user = repository.saveAndFlush(user);
        assertTrue(repository.findById(user.getId()).isPresent());
    }

}
