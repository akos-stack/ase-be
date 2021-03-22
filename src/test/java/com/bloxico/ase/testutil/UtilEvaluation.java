package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageCountryDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.QuotationPackageDto;
import com.bloxico.ase.userservice.entity.address.Region;
import com.bloxico.ase.userservice.entity.evaluation.*;
import com.bloxico.ase.userservice.proj.evaluation.*;
import com.bloxico.ase.userservice.repository.evaluation.*;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.entity.evaluation.QuotationPackage;
import com.bloxico.ase.userservice.entity.evaluation.QuotationPackageCountry;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.proj.evaluation.EvaluableArtworkProj;
import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.repository.evaluation.CountryEvaluationDetailsRepository;
import com.bloxico.ase.userservice.repository.evaluation.QuotationPackageRepository;
import com.bloxico.ase.userservice.service.evaluation.impl.EvaluationServiceImpl;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.WAITING_FOR_EVALUATION;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilEvaluation {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private CountryEvaluationDetailsRepository countryEvaluationDetailsRepository;
    @Autowired private QuotationPackageRepository quotationPackageRepository;
    @Autowired private EvaluationServiceImpl evaluationService;
    @Autowired private ArtworkEvaluatorEvaluationRepository artworkEvaluatorEvaluationRepository;

    public CountryEvaluationDetails genCountryEvaluationDetails(Long countryId) {
        var details = new CountryEvaluationDetails();
        details.setCountryId(countryId);
        details.setPricePerEvaluation(genPosInt(50));
        details.setAvailabilityPercentage(genPosInt(25));
        return details;
    }

    public CountryEvaluationDetailsDto genCountryEvaluationDetailsDto(Long countryId) {
        return MAPPER.toDto(genCountryEvaluationDetails(countryId));
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails() {
        var principalId = utilUser.savedAdmin().getId();
        var countryId = utilLocation.savedCountry().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return countryEvaluationDetailsRepository.saveAndFlush(details);
    }

    public CountryEvaluationDetails savedCountryEvaluationDetails(Long countryId) {
        var principalId = utilUser.savedAdmin().getId();
        var details = genCountryEvaluationDetails(countryId);
        details.setCreatorId(principalId);
        return countryEvaluationDetailsRepository.saveAndFlush(details);
    }

    public CountryEvaluationDetailsDto savedCountryEvaluationDetailsDto() {
        return MAPPER.toDto(savedCountryEvaluationDetails());
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjWithCountryName(
            String countryName)
    {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(
                country.getId(),
                country.getName(),
                List.of(region.getName()),
                details.getId(),
                details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjWithRegion(Region region) {
        var country = utilLocation.savedCountryWithRegion(region);
        var details = savedCountryEvaluationDetails(country.getId());
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(country.getId(), country.getName(),
                List.of(region.getName()), details.getId(), details.getPricePerEvaluation(),
                details.getAvailabilityPercentage(), 0L);
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProj() {
        return savedCountryEvaluationDetailsCountedProjWithCountryName(genUUID());
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(
            String countryName)
    {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithNameAndRegion(countryName, region);
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(
                country.getId(),
                country.getName(),
                List.of(region.getName()),
                null, null, null, 0L);
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjNoDetailsWithRegion(Region region) {
        var country = utilLocation.savedCountryWithRegion(region);
        return new CountryEvaluationDetailsWithEvaluatorsCountProj(country.getId(), country.getName(),
                List.of(region.getName()), null, null, null, 0L);
    }

    public CountryEvaluationDetailsWithEvaluatorsCountProj savedCountryEvaluationDetailsCountedProjNoDetails() {
        return savedCountryEvaluationDetailsCountedProjNoDetailsWithCountryName(genUUID());
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest(String country) {
        return new SaveCountryEvaluationDetailsRequest(country, genPosInt(251), genPosInt(101));
    }

    public SaveCountryEvaluationDetailsRequest genSaveCountryEvaluationDetailsRequest() {
        return genSaveCountryEvaluationDetailsRequest(utilLocation.savedCountry().getName());
    }

    public QuotationPackage genQuotationPackage() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.READY_FOR_EVALUATION)).getId();
        return genQuotationPackage(artworkId);
    }

    public QuotationPackage genQuotationPackage(Long artworkId) {
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

    public QuotationPackage savedQuotationPackage(Long artworkId) {
        var principalId = utilUser.savedAdmin().getId();
        var qPackage = genQuotationPackage(artworkId);
        qPackage.setCreatorId(principalId);
        return quotationPackageRepository.saveAndFlush(qPackage);
    }

    public QuotationPackageDto savedQuotationPackageDto() {
        return MAPPER.toDto(savedQuotationPackage());
    }

    public QuotationPackageCountry genQuotationPackageCountry(long packageId) {
        var countryId = utilLocation.savedCountry().getId();
        return genQuotationPackageCountry(packageId, countryId);
    }

    public QuotationPackageCountry genQuotationPackageCountry(long packageId, long countryId) {
        var qpCountry = new QuotationPackageCountry();
        qpCountry.setNumberOfEvaluations(genPosInt(50));
        qpCountry.setId(new QuotationPackageCountry.Id(packageId, countryId));
        return qpCountry;
    }

    public QuotationPackageCountryDto genQuotationPackageCountryDto(long packageId) {
        return MAPPER.toDto(genQuotationPackageCountry(packageId));
    }

    public QuotationPackageCountryDto genQuotationPackageCountryDto(long packageId, long countryId) {
        return MAPPER.toDto(genQuotationPackageCountry(packageId, countryId));
    }

    public EvaluableArtworkProj savedOngoingEvaluationProj(Long countryId) {
        var artwork = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.READY_FOR_EVALUATION));
        return savedOngoingEvaluationProj(artwork, countryId);
    }

    public EvaluableArtworkProj savedOngoingEvaluationProj(ArtworkDto artwork, Long countryId) {
        var qPackage = savedQuotationPackage(artwork.getId());
        var qpc = genQuotationPackageCountryDto(qPackage.getId(), countryId);
        evaluationService
                .saveQuotationPackageCountries(
                        qPackage.getId(), List.of(qpc));
        return new EvaluableArtworkProj(
                qPackage.getArtworkId(),
                artwork.getTitle(),
                qpc.getNumberOfEvaluations(),
                0
        );
    }

    public EvaluableArtworkProj savedOngoingEvaluationProj() {
        long countryId = utilLocation.savedCountry().getId();
        return savedOngoingEvaluationProj(countryId);
    }

    public SaveQuotationPackageRequest genSaveQuotationPackageRequest() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var c1 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c2 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        var c3 = new SaveQuotationPackageRequest.Country(utilLocation.savedCountry().getId(), genPosInt(50));
        return new SaveQuotationPackageRequest(artworkId, Set.of(c1, c2, c3));
    }

    public UpdateCountryEvaluationDetailsRequest genUpdateCountryEvaluationDetailsRequest(Long id) {
        return new UpdateCountryEvaluationDetailsRequest(id, 40, 15);
    }

    public SearchCountryEvaluationDetailsRequest genSearchCountryEvaluationDetailsRequest() {
        return new SearchCountryEvaluationDetailsRequest("", null);
    }

    public SearchCountryEvaluationDetailsRequest genSearchCountryEvaluationDetailsRequest(List<String> regions) {
        return new SearchCountryEvaluationDetailsRequest("", regions);
    }

    public SearchCountryEvaluationDetailsForManagementRequest genSearchCountryEvaluationDetailsForManagementRequest() {
        return new SearchCountryEvaluationDetailsForManagementRequest("", null);
    }

    public SearchCountryEvaluationDetailsForManagementRequest genSearchCountryEvaluationDetailsForManagementRequest(List<String> regions) {
        return new SearchCountryEvaluationDetailsForManagementRequest("", regions);
    }

    public RegionWithCountriesAndEvaluatorsCountProj savedRegionCountedProj() {
        var region = utilLocation.savedRegion();
        return new RegionWithCountriesAndEvaluatorsCountProj(region.getId(), region.getName(), 0, 0);
    }

    public RegionWithCountriesAndEvaluatorsCountProj savedRegionCountedProj(String regionName) {
        var region = utilLocation.savedRegion(regionName);
        return new RegionWithCountriesAndEvaluatorsCountProj(region.getId(), region.getName(), 0, 0);
    }

    public SearchRegionEvaluationDetailsRequest genDefaultSearchRegionsRequest() {
        return new SearchRegionEvaluationDetailsRequest("");
    }

    public SearchEvaluatedArtworksRequest genSearchEvaluatedArtworksRequest() {
        return new SearchEvaluatedArtworksRequest(null, null);
    }

    public SearchEvaluatedArtworksRequest genSearchEvaluatedArtworksRequest(List<String> categories) {
        return new SearchEvaluatedArtworksRequest(null, categories);
    }

    public SearchEvaluatedArtworksRequest genSearchEvaluatedArtworksRequest(String artworkTitle) {
        return new SearchEvaluatedArtworksRequest(artworkTitle, null);
    }

    public SearchEvaluatedArtworksRequest genSearchEvaluatedArtworksRequest(String artworkTitle, List<String> categories) {
        return new SearchEvaluatedArtworksRequest(artworkTitle, categories);
    }

    public EvaluatedArtworkProj savedEvaluatedArtworkProj() {
        return savedEvaluatedArtworkProj(utilUserProfile.savedEvaluator().getId());
    }

    public EvaluatedArtworkProj savedEvaluatedArtworkProj(long evaluatorId) {
        return savedEvaluatedArtworkProj(utilArtwork.saved(
                utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION)), evaluatorId);
    }

    public EvaluatedArtworkProj savedEvaluatedArtworkProj(ArtworkDto artwork) {
        return savedEvaluatedArtworkProj(artwork, utilUserProfile.savedEvaluator().getId());
    }

    public EvaluatedArtworkProj savedEvaluatedArtworkProj(ArtworkDto artwork, long evaluatorId) {
        var evaluation = new ArtworkEvaluatorEvaluation();
        evaluation.setArtworkId(artwork.getId());
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setCountryId(utilLocation.savedCountry().getId());
        evaluation.setValue(genPosBigDecimal(2000));
        evaluation.setSellingPrice(genPosBigDecimal(2000));
        evaluation.setAseSellable(genBoolean());
        evaluation.setSendOffer(genBoolean());
        evaluation.setRating(genPosInt(6));
        evaluation.setComment(genUUID());
        artworkEvaluatorEvaluationRepository.saveAndFlush(evaluation);
        return new EvaluatedArtworkProj(
                artwork.getTitle(),
                artwork.getArtist().getName(),
                evaluation.getSellingPrice());
    }

    public SearchEvaluableArtworksRequest genSearchEvaluableArtworksRequest() {
        var countryId = utilLocation.savedCountry().getId();
        return genSearchEvaluableArtworksRequest(countryId);
    }
    public SearchEvaluableArtworksRequest genSearchEvaluableArtworksRequest(Long countryId) {
        return new SearchEvaluableArtworksRequest(countryId, "", null);
    }

    public SearchEvaluableArtworksRequest genSearchEvaluableArtworksRequest(Long countryId, String title) {
        return new SearchEvaluableArtworksRequest(countryId, title, null);
    }

}
