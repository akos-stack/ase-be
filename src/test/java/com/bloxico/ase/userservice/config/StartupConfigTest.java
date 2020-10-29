package com.bloxico.ase.userservice.config;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;

public class StartupConfigTest extends AbstractSpringTest {

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

    @Test
    public void JwtBlacklistInMemory_isPopulatedOnStartup() {
        var blacklist = blacklistedJwtRepository.findAll();
        assertFalse(blacklist.isEmpty());
        blacklist
                .stream()
                .map(BlacklistedJwt::getToken)
                .map(JwtBlacklistInMemory::contains)
                .forEach(Assert::assertTrue);
    }

}
