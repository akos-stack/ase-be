package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsCountedProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionCountedProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilEvaluation {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private CountryEvaluationDetailsRepository repository;

    public CountryEvaluationDetails genCountryEvaluationDetails(int countryId) {
        var details = new CountryEvaluationDetails();
        details.setCountryId(countryId);
        details.setPricePerEvaluation(50);
        details.setAvailabilityPercentage(25);
        return details;
    }

    public CountryEvaluationDetails genCountryEvaluationDetails(int countryId, int pricePerEvaluation, int availabilityPercentage) {
        var details = new CountryEvaluationDetails();
        details.setCountryId(countryId);
        details.setPricePerEvaluation(pricePerEvaluation);
        details.setAvailabilityPercentage(availabilityPercentage);
        return details;
    }

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(int countryId) {
        return MAPPER.toDto(genCountryEvaluationDetails(countryId));
    }

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(int countryId, int pricePerEvaluation, int availabilityPercentage) {
        return MAPPER.toDto(genCountryEvaluationDetails(countryId, pricePerEvaluation, availabilityPercentage));
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountry().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return repository.saveAndFlush(details);
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails(int countryId) {
        var principalId = utilUser.savedAdmin().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return repository.saveAndFlush(details);
    }

    public CountryEvaluationDetailsDto savedCountryEvaluationDetailsDto() {
        return MAPPER.toDto(savedCountryEvaluationDetails());
    }

    public CountryEvaluationDetailsCountedProj savedCountryEvaluationDetailsCountedProj() {
        return savedCountryEvaluationDetailsCountedProj(genUUID());
    }

    public CountryEvaluationDetailsCountedProj savedCountryEvaluationDetailsCountedProj(String countryName) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsCountedProj(country.getId(), country.getName(),
                List.of(region.getName()), details.getId(), details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public CountryEvaluationDetailsCountedProj savedCountryEvaluationDetailsCountedProjNoDetails() {
        return savedCountryEvaluationDetailsCountedProjNoDetails(genUUID());
    }

    public CountryEvaluationDetailsCountedProj savedCountryEvaluationDetailsCountedProjNoDetails(String countryName) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        return new CountryEvaluationDetailsCountedProj(country.getId(), country.getName(),
                List.of(region.getName()), null,
                null, null, 0L);
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, 50, 25);
    }

    public UpdateCountryEvaluationDetailsRequest genUpdateCountryEvaluationDetailsRequest(int id) {
        return new UpdateCountryEvaluationDetailsRequest(id, 40, 15);
    }

    public SearchCountryEvaluationDetailsRequest genDefaultSearchCountryEvaluationDetailsRequest() {
        return new SearchCountryEvaluationDetailsRequest(null, "", 0, 10, "country", "asc");
    }

    public RegionCountedProj savedRegionCountedProj() {
        var region = utilLocation.savedRegion();
        return new RegionCountedProj(region.getId(), region.getName(), 0, 0);
    }

    public RegionCountedProj savedRegionCountedProj(String name) {
        var region = utilLocation.savedRegion(name);
        return new RegionCountedProj(region.getId(), region.getName(), 0, 0);
    }

    public SearchRegionsRequest genDefaultSearchRegionsRequest() {
        return new SearchRegionsRequest("", 0, 10, "name", "asc");
    }

}
