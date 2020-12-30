package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.address.City;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertTrue;

public class CityRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private CityRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1).isEmpty());
        var country = mockUtil.savedCountry();
        var city = new City();
        city.setName(uuid());
        city.setCountry(country);
        city.setCreatorId(country.getCreatorId());
        var id = repository.saveAndFlush(city).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByNameIgnoreCase() {
        assertTrue(repository.findByNameIgnoreCase(uuid()).isEmpty());
        var city = mockUtil.savedCity();
        assertTrue(repository.findByNameIgnoreCase(city.getName()).isPresent());
    }

}
