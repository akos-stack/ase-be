package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.entity.evaluation.*;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.evaluation.QuotationPackageRepository;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveQuotationPackageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.bloxico.ase.testutil.Util.genPosInt;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.concurrent.ThreadLocalRandom.current;

@Component
public class UtilEvaluation {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    @Autowired private QuotationPackageRepository quotationPackageRepository;

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
        return countryEvaluationDetailsRepository.saveAndFlush(details);
    }

    public CountryEvaluationDetailsDto savedCountryEvaluationDetailsDto() {
        return MAPPER.toDto(savedCountryEvaluationDetails());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, genPosInt(251), genPosInt(101));
    }

    public QuotationPackage genQuotationPackage() {
        var artworkId = current().nextInt(1, Integer.MAX_VALUE); // TODO
        var qPackage = new QuotationPackage();
        qPackage.setArtworkId(artworkId);
        return qPackage;
    }

    public QuotationPackageDto genQuotationPackageDto() {
        return MAPPER.toDto(genQuotationPackage());
    }

    public QuotationPackage savedQuotationPackage() {
        var principalId = utilUser.savedAdmin().getId();
        var qPackage = genQuotationPackage();
        qPackage.setCreatorId(principalId);
        return quotationPackageRepository.saveAndFlush(qPackage);
    }

    public QuotationPackageDto savedQuotationPackageDto() {
        return MAPPER.toDto(savedQuotationPackage());
    }

    public QuotationPackageCountry genQuotationPackageCountry(long packageId) {
        var countryId = utilLocation.savedCountry().getId();
        var qpCountry = new QuotationPackageCountry();
        qpCountry.setNumberOfEvaluations(genPosInt(50));
        qpCountry.setId(new QuotationPackageCountry.Id(packageId, countryId));
        return qpCountry;
    }

    public QuotationPackageCountryDto genQuotationPackageCountryDto(long packageId) {
        return MAPPER.toDto(genQuotationPackageCountry(packageId));
    }

    public SaveQuotationPackageRequest genSaveQuotationPackageRequest() {
        var artworkId = current().nextInt(1, Integer.MAX_VALUE); // TODO
        var c1 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c2 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c3 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        return new SaveQuotationPackageRequest(artworkId, Set.of(c1, c2, c3));
    }

}
