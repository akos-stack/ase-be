package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilLocation;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.exception.LocationException;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import com.bloxico.ase.userservice.repository.address.RegionRepository;
import com.bloxico.ase.userservice.web.model.address.CreateCountryRequest;
import com.bloxico.ase.userservice.web.model.address.CreateRegionRequest;
import com.bloxico.ase.userservice.web.model.address.EditCountryRequest;
import com.bloxico.ase.userservice.web.model.address.SearchRegionsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private LocationFacadeImpl facade;
    @Autowired private CountryRepository countryRepository;
    @Autowired private RegionRepository regionRepository;

    @Test
    public void findAllCountries() {
        var c1 = utilLocation.savedCountryProj();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1));
        var c2 = utilLocation.savedCountryProj();
        assertThat(facade.findAllCountries().getCountries(), hasItems(c1, c2));
    }

    @Test
    public void findAllCities() {
        var c1 = utilLocation.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1));
        var c2 = utilLocation.savedCityDto();
        assertThat(facade.findAllCities().getCities(), hasItems(c1, c2));
    }

    @Test
    public void findAllRegions_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> facade.findAllRegions(null));
    }

    @Test
    public void findAllRegions() {
        var request = new SearchRegionsRequest();
        var r1 = utilLocation.savedRegionProj();
        assertThat(facade.findAllRegions(request).getRegions(), hasItems(r1));
        var r2 = utilLocation.savedRegionProj();
        assertThat(facade.findAllRegions(request).getRegions(), hasItems(r1, r2));
    }

    @Test
    public void createRegion_nullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> facade.createRegion(null, 1));
    }

    @Test
    public void createRegion_regionAlreadyExists() {
        var regionName = utilLocation.savedRegion().getName();
        var request = new CreateRegionRequest(regionName);
        assertThrows(
                LocationException.class,
                () -> facade.createRegion(request, 1));
    }

    @Test
    public void createRegion() {
        var principalId = utilUser.savedAdmin().getId();
        var regionName = genUUID();
        var request = new CreateRegionRequest(regionName);
        var response = facade.createRegion(request, principalId);

        assertNotNull(response);
        assertNotNull(response.getRegion());

        var newlyCreatedRegion = regionRepository
                .findByNameIgnoreCase(response.getRegion().getName())
                .orElse(null);

        assertNotNull(newlyCreatedRegion);
        assertEquals(regionName, newlyCreatedRegion.getName());
        assertEquals(principalId, newlyCreatedRegion.getCreatorId());
        assertEquals(response.getRegion().getId(), newlyCreatedRegion.getId());
        assertEquals(0, response.getRegion().getNumberOfCountries().intValue());
        assertEquals(0, response.getRegion().getNumberOfEvaluators().intValue());
    }

    @Test
    public void deleteRegion_regionNotFound() {
        assertThrows(
                LocationException.class,
                () -> facade.deleteRegion(-1, utilUser.savedAdmin().getId()));
    }

    @Test
    public void deleteRegion_regionHasCountries() {
        var adminId = utilUser.savedAdmin().getId();
        var region = utilLocation.savedRegionDto();
        var request = new CreateCountryRequest(genUUID(), region.getName(), 10, 40);
        facade.createCountry(request, adminId);
        assertThrows(
                LocationException.class,
                () -> facade.deleteRegion(region.getId(), adminId));
    }

    @Test
    public void deleteRegion_regionSuccessfullyDeleted() {
        var id = utilLocation.savedRegion().getId();
        assertTrue(regionRepository.findById(id).isPresent());
        facade.deleteRegion(id, utilUser.savedAdmin().getId());
        assertTrue(regionRepository.findById(id).isEmpty());
    }

    @Test
    public void createCountry_requestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> facade.createCountry(null, 1));
    }

    @Test
    public void createCountry_countryAlreadyExists() {
        var regionName = utilLocation.savedRegion().getName();
        var request = new CreateCountryRequest(genUUID(), regionName, 10, 40);
        facade.createCountry(request, 1);
        assertThrows(
                LocationException.class,
                () -> facade.createCountry(request, 1));
    }

    @Test
    public void createCountry_regionNotFound() {
        var request = new CreateCountryRequest(genUUID(), genUUID(), 10, 40);
        assertThrows(
                LocationException.class,
                () -> facade.createCountry(request, 1));
    }

    @Test
    public void createCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var name = genUUID();
        var region = utilLocation.savedRegion();

        var request = new CreateCountryRequest(name, region.getName(), 10, 40);
        var response = facade.createCountry(request, principalId);

        assertNotNull(response);
        assertNotNull(response.getCountry());
        var countryResponse = response.getCountry();

        var newlyCreatedCountry = countryRepository
                .findByNameIgnoreCase(name)
                .orElse(null);

        assertNotNull(newlyCreatedCountry);
        assertEquals(countryResponse.getId(), newlyCreatedCountry.getId());
        assertEquals(countryResponse.getName(), newlyCreatedCountry.getName());
        assertEquals(countryResponse.getRegion().getName(), newlyCreatedCountry.getRegion().getName());
        assertEquals(principalId, newlyCreatedCountry.getCreatorId());
        assertEquals(10, countryResponse.getCountryEvaluationDetails().getPricePerEvaluation());
        assertEquals(40, countryResponse.getCountryEvaluationDetails().getAvailabilityPercentage());
        assertEquals(0, countryResponse.getCountryEvaluationDetails().getTotalOfEvaluators());
    }

    @Test
    public void editCountry_requestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> facade.editCountry(null, 1, 1));
            }

    @Test
    public void editCountry_countryAlreadyExists() {
        var country1 = utilLocation.savedCountryDto();
        var country2 = utilLocation.savedCountryDto();
        var request = new EditCountryRequest(
                country2.getName(), country1.getRegion().getName(), 5, 55);
        assertThrows(
                LocationException.class,
                () -> facade.editCountry(request, country1.getId(), 1));
    }

    @Test
    public void editCountry_regionNotFound() {
        var countryId = utilLocation.savedCountryDto().getId();
        var request = new EditCountryRequest(genUUID(), genUUID(), 5, 55);
        assertThrows(
                LocationException.class,
                () -> facade.editCountry(request, countryId, 1));
    }

    @Test
    public void editCountry() {
        var principalId = utilUser.savedAdmin().getId();
        var name = genUUID();
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountry();

        var request = new EditCountryRequest(name, region.getName(), 5, 55);
        facade.editCountry(request, country.getId(), principalId);

        var editedCountry = countryRepository
                .findByNameIgnoreCase(name)
                .orElse(null);

        assertNotNull(editedCountry);
        assertEquals(name, editedCountry.getName());
        assertEquals(region.getName(), editedCountry.getRegion().getName());
        assertEquals(5, editedCountry.getCountryEvaluationDetails().getPricePerEvaluation());
        assertEquals(55, editedCountry.getCountryEvaluationDetails().getAvailabilityPercentage());
        assertEquals(principalId, editedCountry.getUpdaterId());
    }

}
