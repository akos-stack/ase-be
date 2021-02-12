package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.address.CityDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.bloxico.ase.userservice.projection.RegionDetailsProj;
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
    @Autowired private RegionRepository regionRepository;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;

    public Region savedRegion() {
        var creatorId = utilUser.savedAdmin().getId();
        var region = new Region();
        region.setName(genUUID());
        region.setCreatorId(creatorId);
        return regionRepository.saveAndFlush(region);
    }

    public Region savedRegionWithName(String name) {
        var creatorId = utilUser.savedAdmin().getId();
        var region = new Region();
        region.setName(name);
        region.setCreatorId(creatorId);
        return regionRepository.saveAndFlush(region);
    }

    public RegionDto savedRegionDto() {
        return MAPPER.toDto(savedRegion());
    }

    public RegionDto savedRegionDtoWithName(String name) {
        return MAPPER.toDto(savedRegionWithName(name));
    }

    public RegionDto genRegionDto() {
        var regionDto = new RegionDto();
        regionDto.setName(genUUID());
        return regionDto;
    }

    public RegionDto genRegionDtoWithName(String name) {
        var regionDto = new RegionDto();
        regionDto.setName(name);
        return regionDto;
    }

    public RegionDetailsProj savedRegionProj() {
        var regionDto = savedRegionDto();
        return new RegionDetailsProj(
                regionDto.getId(), regionDto.getName(),
                0, 0
        );
    }

    public RegionDetailsProj savedRegionProjWithName(String name) {
        var regionDto = savedRegionDtoWithName(name);
        return new RegionDetailsProj(
                regionDto.getId(), regionDto.getName(),
                0, 0
        );
    }

    public Country savedCountry() {
        var creatorId = utilUser.savedAdmin().getId();
        var region = savedRegion();
        var country = new Country();
        country.setName(genUUID());
        country.setRegion(region);
        country.setCreatorId(creatorId);
        countryRepository.saveAndFlush(country);
        var evaluationDetails = savedCountryEvaluationDetails(country);
        country.setCountryEvaluationDetails(evaluationDetails);
        return country;
    }

    public CountryDto genCountryDtoWithRegionDto(RegionDto regionDto) {
        var countryDto = new CountryDto();
        countryDto.setName(genUUID());
        countryDto.setRegion(regionDto);
        return countryDto;
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails(Country country) {
        var evaluationDetails = new CountryEvaluationDetails();
        evaluationDetails.setPricePerEvaluation(10);
        evaluationDetails.setAvailabilityPercentage(40);
        evaluationDetails.setCountry(country);
        evaluationDetails.setCreatorId(country.getCreatorId());
        countryEvaluationDetailsRepository.saveAndFlush(evaluationDetails);
        return evaluationDetails;
    }

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto() {
        var evaluationDetailsDto = new CountryEvaluationDetailsDto();
        evaluationDetailsDto.setPricePerEvaluation(10);
        evaluationDetailsDto.setAvailabilityPercentage(40);
        return evaluationDetailsDto;
    }

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(int price, int availability) {
        var evaluationDetailsDto = new CountryEvaluationDetailsDto();
        evaluationDetailsDto.setPricePerEvaluation(price);
        evaluationDetailsDto.setAvailabilityPercentage(availability);
        return evaluationDetailsDto;
    }

    public CountryTotalOfEvaluatorsProj savedCountryProj() {
        var countryDto = savedCountryDto();
        return new CountryTotalOfEvaluatorsProj(
                countryDto.getId(), countryDto.getName(), countryDto.getRegion().getName(),
                countryDto.getCountryEvaluationDetails().getPricePerEvaluation(),
                countryDto.getCountryEvaluationDetails().getAvailabilityPercentage(),
                0
        );
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
        var country = savedCountry();
        var location = new Location();
        location.setCountry(country);
        location.setAddress(genUUID());
        location.setCreatorId(country.getCreatorId());
        return locationRepository.saveAndFlush(location);
    }

}
