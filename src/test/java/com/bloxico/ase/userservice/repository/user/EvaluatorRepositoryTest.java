package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.user.Evaluator;
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
        var creator = mockUtil.savedUserProfile();
        evaluator.setUserProfile(creator);
        evaluator.setCreatorId(creator.getId());
        var id = repository.saveAndFlush(evaluator).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
