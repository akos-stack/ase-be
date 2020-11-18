package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;

import java.util.Collection;
import java.util.List;

public interface IUserRegistrationService {

    UserProfileDto registerDisabledUser(RegistrationRequest request);

    void enableUser(long id);

    List<Long> deleteDisabledUsersWithIds(Collection<Long> ids);

}
