package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.address.Country;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertTrue;

public class CountryRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private CountryRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1).isEmpty());
        var creatorId = mockUtil.savedAdmin().getId();
        var country = new Country();
        country.setName(uuid());
        country.setCreatorId(creatorId);
        var id = repository.saveAndFlush(country).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByNameIgnoreCase() {
        assertTrue(repository.findByNameIgnoreCase(uuid()).isEmpty());
        var country = mockUtil.savedCountry();
        assertTrue(repository.findByNameIgnoreCase(country.getName()).isPresent());
    }

}
