package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.user.SearchUsersRequest;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface IUserService {

    UserDto findUserById(long id);

    UserDto findUserByEmail(String email);

    Page<UserDto> findUsersByEmailOrRole(SearchUsersRequest request, PageRequest page);

    UserDto saveUser(UserDto userDto);

    void enableUser(long userId, long principalId);

    void disableUser(long userId);

    List<Long> deleteDisabledUsersWithIds(Collection<Long> ids);

    void updatePassword(long principalId, String oldPassword, String newPassword);

    void setNewPassword(long principalId, String password);

}
