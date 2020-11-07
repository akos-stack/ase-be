package com.bloxico.ase.userservice.config;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.token.BlacklistedJwt;
import com.bloxico.ase.userservice.repository.token.BlacklistedJwtRepository;
import com.bloxico.ase.userservice.util.JwtBlacklistInMemory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StartupConfigTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private StartupConfig startupConfig;

    @Autowired
    private BlacklistedJwtRepository blacklistedJwtRepository;

    @Test
    public void JwtBlacklistInMemory_isPopulatedOnStartup() {
        String token = "7WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ";

        assertFalse(JwtBlacklistInMemory.contains(token));
        startupConfig.initJwtBlacklistInMemory();
        assertFalse(JwtBlacklistInMemory.contains(token));

        BlacklistedJwt jwt = new BlacklistedJwt();
        jwt.setToken(token);
        jwt.setCreatorId(mockUtil.savedAdmin().getId());
        blacklistedJwtRepository.save(jwt);

        assertFalse(JwtBlacklistInMemory.contains(token));
        startupConfig.initJwtBlacklistInMemory();
        assertTrue(JwtBlacklistInMemory.contains(token));
    }

}
