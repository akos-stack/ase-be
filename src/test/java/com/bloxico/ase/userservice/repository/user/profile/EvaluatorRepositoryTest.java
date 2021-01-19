package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.profile.Evaluator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class EvaluatorRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private EvaluatorRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var evaluator = new Evaluator();
        var userProfile = mockUtil.savedUserProfile();
        evaluator.setUserProfile(userProfile);
        evaluator.setCreatorId(userProfile.getUserId());
        var id = repository.saveAndFlush(evaluator).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
