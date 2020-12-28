package com.bloxico.ase.userservice.service.user.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.user.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.exception.UserProfileException;
import com.bloxico.ase.userservice.web.model.user.UpdateUserProfileRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Set;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.entity.user.Role.EVALUATOR;
import static com.bloxico.ase.userservice.entity.user.Role.USER;
import static org.junit.Assert.*;

public class UserProfileServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Test(expected = UserProfileException.class)
    public void findUserProfileById_notFound() {
        userProfileService.findUserProfileById(-1);
    }

    @Test
    public void findUserProfileById_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileById(userProfileDto.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void findUserProfileByEmail_nullEmail() {
        userProfileService.findUserProfileByEmail(null);
    }

    @Test(expected = UserProfileException.class)
    public void findUserProfileByEmail_notFound() {
        userProfileService.findUserProfileByEmail(uuid());
    }

    @Test
    public void findUserProfileByEmail_found() {
        var userProfileDto = mockUtil.savedUserProfileDto();
        assertEquals(
                userProfileDto,
                userProfileService.findUserProfileByEmail(userProfileDto.getEmail()));
    }

    @Test(expected = NullPointerException.class)
    public void updateUserProfile_nullRequest() {
        userProfileService.updateUserProfile(1, null);
    }

    @Test(expected = UserProfileException.class)
    public void updateUserProfile_notFound() {
        var request = new UpdateUserProfileRequest("John", null);
        userProfileService.updateUserProfile(-1, request);
    }

    @Test
    public void updateUserProfile() {
        var id = mockUtil.savedUserProfile().getId();
        var request = new UpdateUserProfileRequest("John", "007008123");
        var updated = userProfileService.updateUserProfile(id, request);
        assertEquals(request.getName(), updated.getName());
        assertEquals(request.getPhone(), updated.getPhone());
    }

    @Test(expected = NullPointerException.class)
    public void saveEnabledUserProfile_nullDto() {
        userProfileService.saveEnabledUserProfile(null, 1);
    }

    @Test
    public void saveEnabledUserProfile() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = new UserProfileDto();
        userProfileDto.setName(uuid());
        userProfileDto.setEmail(uuid());
        userProfileDto.setPassword(uuid());
        userProfileDto.setFirstName(uuid());
        userProfileDto.setLastName(uuid());
        userProfileDto.setBirthday(LocalDate.now().minusYears(20));
        userProfileDto.setGender(uuid());
        userProfileDto.setPhone(uuid());
        userProfileDto.setLocation(mockUtil.savedLocationDto());
        userProfileDto = userProfileService.saveEnabledUserProfile(userProfileDto, principalId);
        assertTrue(userProfileDto.getEnabled());
        assertEquals(Set.of(), userProfileDto.getRoles());
    }

    @Test(expected = IllegalStateException.class)
    public void saveEvaluator_missingRole() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = new UserProfileDto();
        {
            userProfileDto.setName(uuid());
            userProfileDto.setEmail(uuid());
            userProfileDto.setPassword(uuid());
            userProfileDto.setFirstName(uuid());
            userProfileDto.setLastName(uuid());
            userProfileDto.setBirthday(LocalDate.now().minusYears(20));
            userProfileDto.setGender(uuid());
            userProfileDto.setPhone(uuid());
            userProfileDto.setLocation(mockUtil.savedLocationDto());
            userProfileDto.addRole(rolePermissionService.findRoleByName(USER));
            userProfileDto = userProfileService.saveEnabledUserProfile(userProfileDto, principalId);
        }
        var evaluatorDto = new EvaluatorDto();
        evaluatorDto.setUserProfile(userProfileDto);
        userProfileService.saveEvaluator(evaluatorDto, principalId);
    }

    @Test
    public void saveEvaluator() {
        var principalId = mockUtil.savedAdmin().getId();
        var userProfileDto = new UserProfileDto();
        {
            userProfileDto.setName(uuid());
            userProfileDto.setEmail(uuid());
            userProfileDto.setPassword(uuid());
            userProfileDto.setFirstName(uuid());
            userProfileDto.setLastName(uuid());
            userProfileDto.setBirthday(LocalDate.now().minusYears(20));
            userProfileDto.setGender(uuid());
            userProfileDto.setPhone(uuid());
            userProfileDto.setLocation(mockUtil.savedLocationDto());
            userProfileDto.addRole(rolePermissionService.findRoleByName(EVALUATOR));
            userProfileDto = userProfileService.saveEnabledUserProfile(userProfileDto, principalId);
        }
        var evaluatorDto = new EvaluatorDto();
        evaluatorDto.setUserProfile(userProfileDto);
        evaluatorDto = userProfileService.saveEvaluator(evaluatorDto, principalId);
        assertNotNull(evaluatorDto.getId());
    }

    @Test(expected = UserProfileException.class)
    public void disableUser_notFound() {
        var adminId = mockUtil.savedAdmin().getId();
        userProfileService.disableUser(-1, adminId);
    }

    @Test
    public void disableUser() {
        var adminId = mockUtil.savedAdmin().getId();
        var userId = mockUtil.savedUserProfile().getId();
        assertTrue(userProfileService.findUserProfileById(userId).getEnabled());
        userProfileService.disableUser(userId, adminId);
        assertFalse(userProfileService.findUserProfileById(userId).getEnabled());
    }

    @Test
    public void findUsersByEmail() {
        mockUtil.saveUserProfiles();
        assertTrue(userProfileService.findUsersByEmail("user1", 0, 100, "name").size() == 1);
    }

}
