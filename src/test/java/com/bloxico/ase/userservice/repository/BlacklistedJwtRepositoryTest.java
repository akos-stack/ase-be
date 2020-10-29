package com.bloxico.ase.userservice.repository;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class BlacklistedJwtRepositoryTest extends AbstractSpringTest {

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

    @Test
    public void save() {
        BlacklistedJwt jwt = new BlacklistedJwt();
        jwt.setToken("9WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ");
        jwt.setCreator(1L);
        blacklistedJwtRepository.save(jwt);
    }

    @Test
    public void findAll() {
        assertEquals(
                Set.of("7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ",
                        "8WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ"),
                blacklistedJwtRepository
                        .findAll()
                        .stream()
                        .map(BlacklistedJwt::getToken)
                        .collect(toSet()));
    }

}
