package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
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

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(int countryId) {
        return MAPPER.toDto(genCountryEvaluationDetails(countryId));
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountry().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return repository.saveAndFlush(details);
    }

    public CountryEvaluationDetailsDto savedCountryEvaluationDetailsDto() {
        return MAPPER.toDto(savedCountryEvaluationDetails());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, 50, 25);
    }

}
