package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlacklistedJwtRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private BlacklistedJwtRepository repository;

    @Test
    public void save() {
        BlacklistedJwt jwt = new BlacklistedJwt();
        jwt.setToken("7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt.setCreatorId(mockUtil.savedUserProfile().getId());
        repository.saveAndFlush(jwt);
        assertNotNull(jwt.getId());
    }

    @Test
    public void findById() {
        assertTrue(repository.findById(-1L).isEmpty());
        BlacklistedJwt jwt = new BlacklistedJwt();
        jwt.setToken("7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt.setCreatorId(mockUtil.savedUserProfile().getId());
        jwt = repository.saveAndFlush(jwt);
        assertTrue(repository.findById(jwt.getId()).isPresent());
    }

}
