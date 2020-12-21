package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.config.security.AsePrincipal.newUserDetails;
import static org.junit.Assert.assertEquals;

public class AsePrincipalServiceTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private AsePrincipalService asePrincipalService;

    @Test(expected = NullPointerException.class)
    public void loadUserByUsername_nullEmail() {
        asePrincipalService.loadUserByUsername(null);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_notFound() {
        asePrincipalService.loadUserByUsername(uuid());
    }

    @Test
    public void loadUserByUsername_found() {
        var userProfile = mockUtil.savedUserProfile();
        assertEquals(
                newUserDetails(userProfile),
                asePrincipalService.loadUserByUsername(userProfile.getEmail()));
    }

}
