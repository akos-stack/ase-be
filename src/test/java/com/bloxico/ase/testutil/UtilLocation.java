package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.repository.address.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilLocation {

    @Autowired private UtilUser utilUser;
    @Autowired private CountryRepository countryRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private RegionRepository regionRepository;

    public Region savedRegion() {
        var creatorId = utilUser.savedAdmin().getId();
        var region = new Region();
        region.setName(genUUID());
        region.setCreatorId(creatorId);
        return regionRepository.saveAndFlush(region);
    }

    public RegionDto savedRegionDto() {
        return MAPPER.toDto(savedRegion());
    }

    public Country savedCountry() {
        var creatorId = utilUser.savedAdmin().getId();
        var region = savedRegion();
        var country = new Country();
        country.setName(genUUID());
        country.setRegion(region);
        country.setCreatorId(creatorId);
        countryRepository.saveAndFlush(country);
        return country;
    }

    public CountryDto savedCountryDto() {
        return MAPPER.toDto(savedCountry());
    }

    public Location savedLocation() {
        var country = savedCountry();
        var location = new Location();
        location.setCountry(country);
        location.setAddress(genUUID());
        location.setCreatorId(country.getCreatorId());
        return locationRepository.saveAndFlush(location);
    }

}
