package com.bloxico.ase.userservice.repository;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class BlacklistedJwtRepositoryTest extends AbstractSpringTest {

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

    @Test
    public void save() {
        BlacklistedJwt jwt = new BlacklistedJwt();
        jwt.setToken("7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt.setCreator(1L);
        blacklistedJwtRepository.save(jwt);
    }

    @Test
    public void findAll() {
        BlacklistedJwt jwt1 = new BlacklistedJwt();
        jwt1.setToken("7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt1.setCreator(1L);
        BlacklistedJwt jwt2 = new BlacklistedJwt();
        jwt2.setToken("8WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt2.setCreator(1L);
        List<BlacklistedJwt> tokens = List.of(jwt1, jwt2);
        assertEquals(emptyList(), blacklistedJwtRepository.findAll());
        blacklistedJwtRepository.saveAll(tokens);
        assertEquals(tokens, blacklistedJwtRepository.findAll());
    }

}
