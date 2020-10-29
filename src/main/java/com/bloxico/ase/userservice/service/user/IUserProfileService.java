package com.bloxico.ase.userservice.service.user;

import com.bloxico.ase.userservice.dto.entity.user.UserProfileDto;

public interface IUserProfileService {

    UserProfileDto findUserProfileByEmail(String email);

    void checkPassword(String raw, String encoded);

}
