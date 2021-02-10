package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.address.Location;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertTrue;

public class LocationRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private LocationRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1L).isEmpty());
        var country = mockUtil.savedCountry();
        var location = new Location();
        location.setCountry(country);
        location.setAddress(uuid());
        location.setCreatorId(country.getCreatorId());
        var id = repository.saveAndFlush(location).getId();
        assertTrue(repository.findById(id).isPresent());
    }

}
