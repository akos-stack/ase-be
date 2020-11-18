package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.repository.user.UserProfileRepository;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UserRegistrationServiceImplTest extends AbstractSpringTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRegistrationServiceImpl userRegistrationService;

    @Test(expected = NullPointerException.class)
    public void registerDisabledUser_nullRequest() {
        userRegistrationService.registerDisabledUser(null);
    }

    @Test(expected = UserProfileException.class)
    public void registerDisabledUser_passwordMismatch() {
        var request = new RegistrationRequest("passwordMismatch@mail.com", "Password1!", "Password2!");
        userRegistrationService.registerDisabledUser(request);
    }

    @Test(expected = UserProfileException.class)
    public void registerDisabledUser_userAlreadyExists() {
        var request1 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        var request2 = new RegistrationRequest("temp@mail.com", "Password1!", "Password1!");
        userRegistrationService.registerDisabledUser(request1);
        userRegistrationService.registerDisabledUser(request2);
    }

    @Test
    public void registerDisabledUser() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var user = userRegistrationService.registerDisabledUser(request);
        assertNotNull(user.getId());
        assertEquals(request.getEmail(), user.getEmail());
        assertTrue(request.getEmail().contains(user.getName()));
        assertNotEquals(request.getPassword(), user.getPassword());
        assertTrue(user.streamRoleNames().anyMatch(Role.USER::equals));
    }

    @Test(expected = UserProfileException.class)
    public void enableUser_notFound() {
        userRegistrationService.enableUser(-1);
    }

    @Test
    public void enableUser() {
        var request = new RegistrationRequest("passwordMatches@mail.com", "Password1!", "Password1!");
        var regUser = userRegistrationService.registerDisabledUser(request);
        assertFalse(regUser.getEnabled());
        userRegistrationService.enableUser(regUser.getId());
        var ebdUser = userProfileRepository.findById(regUser.getId()).orElseThrow();
        assertTrue(ebdUser.getEnabled());
        assertEquals(regUser.getId(), ebdUser.getUpdaterId());
    }

    @Test(expected = NullPointerException.class)
    public void deleteDisabledUsersWithIds_nullIds() {
        userRegistrationService.deleteDisabledUsersWithIds(null);
    }

    @Test
    public void deleteDisabledUsersWithIds_emptyIds() {
        assertTrue(userRegistrationService.deleteDisabledUsersWithIds(List.of()).isEmpty());
    }

    @Test
    public void deleteDisabledUsersWithIds() {
        var req1 = new RegistrationRequest("temp1@mail.com", "Password1!", "Password1!");
        var req2 = new RegistrationRequest("temp2@mail.com", "Password1!", "Password1!");
        var req3 = new RegistrationRequest("temp3@mail.com", "Password1!", "Password1!");
        var user1 = userRegistrationService.registerDisabledUser(req1);
        var user2 = userRegistrationService.registerDisabledUser(req2);
        var user3 = userRegistrationService.registerDisabledUser(req3);
        userRegistrationService.enableUser(user1.getId());
        var regIds = Set.of(user1.getId(), user2.getId(), user3.getId());
        var delIds = Set.copyOf(userRegistrationService.deleteDisabledUsersWithIds(regIds));
        assertEquals(Set.of(user2.getId(), user3.getId()), delIds);
    }

}
