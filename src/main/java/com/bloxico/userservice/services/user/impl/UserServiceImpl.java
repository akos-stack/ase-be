package com.bloxico.userservice.services.user.impl;

import com.bloxico.userservice.dto.entities.CoinUserDto;
import com.bloxico.userservice.dto.entities.RegionDto;
import com.bloxico.userservice.entities.user.CoinUser;
import com.bloxico.userservice.entities.user.Region;
import com.bloxico.userservice.exceptions.CoinUserException;
import com.bloxico.userservice.repository.user.CoinUserRepository;
import com.bloxico.userservice.repository.user.RegionRepository;
import com.bloxico.userservice.services.user.IUserService;
import com.bloxico.userservice.util.mappers.EntityDataMapper;
import com.bloxico.userservice.web.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service("userService")
public class UserServiceImpl implements IUserService {

    private CoinUserRepository coinUserRepository;
    private RegionRepository regionRepository;
    private EntityDataMapper entityDataMapper = EntityDataMapper.INSTANCE;

    @Autowired
    public UserServiceImpl(CoinUserRepository coinUserRepository, RegionRepository regionRepository) {
        this.coinUserRepository = coinUserRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public CoinUserDto findUserByEmail(String email) {
        log.debug("Find user by email - start , email: {}", email);

        Optional<CoinUser> op = coinUserRepository.findByEmailIgnoreCase(email);

        CoinUser coinUser = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        log.debug("Find user by email - end , email: {}", email);
        return entityDataMapper.coinUserToDto(coinUser);
    }

    @Override
    public CoinUserDto findUserByUserId(long id) {
        log.debug("Find user by user id - start , user id: {}", id);

        Optional<CoinUser> op = coinUserRepository.findById(id);
        CoinUser coinUser = op.orElseThrow(() -> new CoinUserException(ErrorCodes.USER_DOES_NOT_EXIST.getCode()));

        log.debug("Find user by user id - start , user id: {}", id);
        return entityDataMapper.coinUserToDto(coinUser);
    }

    @Override
    public List<RegionDto> getRegionList() {
        log.debug("Get region list - start");

        List<Region> regions = regionRepository.findAllByOrderByRegionNameAsc();
        List<RegionDto> regionDtos = entityDataMapper.regionsToDtos(regions);

        log.debug("Get region list - end");
        return regionDtos;
    }
}
