package com.bloxico.userservice.services.user;

import com.bloxico.userservice.dto.RegistrationRequestDto;
import com.bloxico.userservice.dto.entities.CoinUserDto;

import java.util.List;

public interface IUserRegistrationService {

    CoinUserDto registerDisabledUser(RegistrationRequestDto registrationRequestDto);

    void enableUser(long userId);

    List<Long> deleteDisabledUsersWithIds(List<Long> ids);

    void integrateUserIfNotPresent(String email);
}
