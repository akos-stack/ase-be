package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface IUserService {

    UserDto findUserById(long id);

    UserDto findUserByEmail(String email);

    List<UserDto> findUsersByEmailOrRole(String email, String role, int page, int size, String sort);

    UserDto saveDisabledUser(UserDto userDto);

    UserDto saveEnabledUser(UserDto userDto);

    void enableUser(long id);

    void disableUser(long userId, long principalId);

    List<Long> deleteDisabledUsersWithIds(Collection<Long> ids);

    void updatePassword(long principalId, String oldPassword, String newPassword);

    void setNewPassword(long principalId, String password);

}
