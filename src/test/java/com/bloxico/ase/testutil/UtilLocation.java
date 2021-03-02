package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.entity.address.Location;
import com.bloxico.ase.userservice.entity.address.Region;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.LocationRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilLocation {

    @Autowired private UtilUser utilUser;
    @Autowired private CountryRepository countryRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private RegionRepository regionRepository;

    public Region savedRegion() {
        return savedRegion(genUUID());
    }

    public Region savedRegion(String name) {
        var creatorId = utilUser.savedAdmin().getId();
        var region = new Region();
        region.setName(name);
        region.setCreatorId(creatorId);
        return regionRepository.saveAndFlush(region);
    }

    public RegionDto savedRegionDto() {
        return MAPPER.toDto(savedRegion());
    }

    public Country savedCountry() {
        return savedCountry(genUUID());
    }

    public Country savedCountry(String name) {
        return savedCountryWithNameAndRegion(name, savedRegion());
    }

    public Country savedCountryWithRegion(Region region) {
        return savedCountryWithNameAndRegion(genUUID(), region);
    }

    public Country savedCountryWithNameAndRegion(String name, Region region) {
        var creatorId = utilUser.savedAdmin().getId();
        var country = new Country();
        country.setName(name);
        country.setRegions(Set.of(region));
        country.setCreatorId(creatorId);
        countryRepository.saveAndFlush(country);
        return country;
    }

    public CountryDto savedCountryDto() {
        return MAPPER.toDto(savedCountry());
    }

    public CountryDto genCountryDto(Long id, String name, Set<RegionDto> regions) {
        var dto = new CountryDto();
        dto.setId(id);
        dto.setName(name);
        dto.setRegions(regions);
        return dto;
    }

    public Location savedLocation() {
        var country = savedCountry();
        var location = new Location();
        location.setCountry(country);
        location.setAddress(genUUID());
        location.setCreatorId(country.getCreatorId());
        return locationRepository.saveAndFlush(location);
    }

    public LocationDto savedLocationDto() {
        return MAPPER.toDto(savedLocation());
    }

}
