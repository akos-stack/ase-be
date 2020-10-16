package com.bloxico.userservice.services.user;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.RegionDto;

import java.util.List;

public interface IUserService {

    CoinUserDto findUserByEmail(String email);

    CoinUserDto findUserByUserId(long id);

    List<RegionDto> getRegionList();
}
