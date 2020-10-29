package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.exception.UserProfileException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test(expected = NullPointerException.class)
    public void findUserProfileByEmail_nullEmail() {
        userProfileService.findUserProfileByEmail(null);
    }

    @Test(expected = UserProfileException.class)
    public void findUserProfileByEmail_notFound() {
        userProfileService.findUserProfileByEmail(UUID.randomUUID().toString());
    }

    @Test
    public void findUserProfileByEmail_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileByEmail(userProfileDto.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void checkPassword_nullRaw() {
        userProfileService.checkPassword(null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void checkPassword_nullEncoded() {
        userProfileService.checkPassword("foo", null);
    }

    @Test(expected = NullPointerException.class)
    public void checkPassword_nullBoth() {
        userProfileService.checkPassword(null, null);
    }

    @Test(expected = UserProfileException.class)
    public void checkPassword_mismatch() {
        userProfileService.checkPassword(
                "foo",
                passwordEncoder.encode("bar"));
    }

    @Test
    public void checkPassword_match() {
        userProfileService.checkPassword(
                "foo",
                passwordEncoder.encode("foo"));
    }

}
