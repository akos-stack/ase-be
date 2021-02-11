package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
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
    @Autowired private CityRepository cityRepository;
    @Autowired private LocationRepository locationRepository;

    public Country savedCountry() {
        var creatorId = utilUser.savedAdmin().getId();
        var country = new Country();
        country.setName(genUUID());
        country.setCreatorId(creatorId);
        return countryRepository.saveAndFlush(country);
    }

    public CountryDto savedCountryDto() {
        return MAPPER.toDto(savedCountry());
    }

    public City savedCity() {
        var country = savedCountry();
        var city = new City();
        city.setCountry(country);
        city.setName(genUUID());
        city.setZipCode(genUUID());
        city.setCreatorId(country.getCreatorId());
        return cityRepository.saveAndFlush(city);
    }

    public CityDto savedCityDto() {
        return MAPPER.toDto(savedCity());
    }

    public Location savedLocation() {
        var city = savedCity();
        var location = new Location();
        location.setCity(city);
        location.setAddress(genUUID());
        location.setCreatorId(city.getCreatorId());
        return locationRepository.saveAndFlush(location);
    }

}
