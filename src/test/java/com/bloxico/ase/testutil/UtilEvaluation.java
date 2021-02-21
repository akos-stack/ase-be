package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.UpdateCountryEvaluationDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        var country = utilLocation.savedCountry();
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsCountedProj(country.getName(),
                country.getRegion().getName(), details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public CountryEvaluationDetailsCountedProj savedCountryEvaluationDetailsCountedProj(String countryName) {
        var country = utilLocation.savedCountry(countryName);
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsCountedProj(country.getName(),
                country.getRegion().getName(), details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, 50, 25);
    }

    public UpdateCountryEvaluationDetailsRequest genUpdateCountryEvaluationDetailsRequest() {
        return new UpdateCountryEvaluationDetailsRequest(40, 15);
    }

    public SearchCountryEvaluationDetailsRequest genDefaultSearchCountryEvaluationDetailsRequest() {
        return new SearchCountryEvaluationDetailsRequest(null, "", 0, 10, "country", "asc");
    }
}
