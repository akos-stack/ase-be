package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageCountryDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.entity.evaluation.QuotationPackage;
import com.bloxico.ase.userservice.entity.evaluation.QuotationPackageCountry;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.evaluation.QuotationPackageRepository;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.genPosInt;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilEvaluation {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    @Autowired private QuotationPackageRepository quotationPackageRepository;

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

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(
            int countryId, int pricePerEvaluation, int availabilityPercentage) {
        return MAPPER.toDto(genCountryEvaluationDetails(countryId, pricePerEvaluation, availabilityPercentage));
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountry().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return countryEvaluationDetailsRepository.saveAndFlush(details);
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails(int countryId) {
        var principalId = utilUser.savedAdmin().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return countryEvaluationDetailsRepository.saveAndFlush(details);
    }

    public CountryEvaluationDetailsDto savedCountryEvaluationDetailsDto() {
        return MAPPER.toDto(savedCountryEvaluationDetails());
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProj() {
        return savedCountryEvaluationDetailsCountedProj(genUUID());
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProj(String countryName) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(country.getId(), country.getName(),
                List.of(region.getName()), details.getId(), details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjNoDetails() {
        return savedCountryEvaluationDetailsCountedProjNoDetails(genUUID());
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjNoDetails(String countryName) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(country.getId(), country.getName(),
                List.of(region.getName()), null,
                null, null, 0L);
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, genPosInt(251), genPosInt(101));
    }

    public QuotationPackage genQuotationPackage() {
        var artworkId = utilArtwork.savedArtworkDto().getId();
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
        var artworkId = utilArtwork.savedArtworkDto().getId();
        var c1 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c2 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c3 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        return new SaveQuotationPackageRequest(artworkId, Set.of(c1, c2, c3));
    }

    public UpdateCountryEvaluationDetailsRequest genUpdateCountryEvaluationDetailsRequest(int id) {
        return new UpdateCountryEvaluationDetailsRequest(id, 40, 15);
    }

    public SearchCountryEvaluationDetailsRequest genDefaultSearchCountryEvaluationDetailsRequest() {
        return new SearchCountryEvaluationDetailsRequest(null, "", 0, 10, "country", "asc");
    }

    public RegionWithCountriesAndEvaluatorsCountProj savedRegionCountedProj() {
        var region = utilLocation.savedRegion();
        return new RegionWithCountriesAndEvaluatorsCountProj(region.getId(), region.getName(), 0, 0);
    }

    public RegionWithCountriesAndEvaluatorsCountProj savedRegionCountedProj(String name) {
        var region = utilLocation.savedRegion(name);
        return new RegionWithCountriesAndEvaluatorsCountProj(region.getId(), region.getName(), 0, 0);
    }

    public SearchRegionsRequest genDefaultSearchRegionsRequest() {
        return new SearchRegionsRequest("", 0, 10, "name", "asc");
    }

}
