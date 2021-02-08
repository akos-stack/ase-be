package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.copyBaseEntityData;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseEntityTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void prePersist_creator_isNull() {
        var country = new Country();
        country.setName(uuid());
        assertThrows(
                NullPointerException.class,
                () -> countryRepository.saveAndFlush(country));
    }

    @Test
    public void prePersist_creator_isNotNull() {
        var country = new Country();
        var creator = mockUtil.savedUser();
        country.setName(uuid());
        country.setCreatorId(creator.getId());
        assertNull(country.getCreatedAt());
        country = countryRepository.saveAndFlush(country);
        assertNotNull(country.getCreatedAt());
    }

    @Test
    public void preUpdate_updater_isNull() {
        var oldCountry = new Country();
        {
            var creator = mockUtil.savedUser();
            oldCountry.setName(uuid());
            oldCountry.setCreatorId(creator.getId());
            countryRepository.saveAndFlush(oldCountry);
        }
        var newCountry = new Country();
        {
            newCountry.setId(oldCountry.getId());
            newCountry.setName(uuid()); // update
            copyBaseEntityData(oldCountry, newCountry);
            assertThrows(
                    NullPointerException.class,
                    () -> countryRepository.saveAndFlush(newCountry));
        }
    }

    @Test
    public void preUpdate_updater_isNotNull() {
        var oldCountry = new Country();
        {
            var creator = mockUtil.savedUser();
            oldCountry.setName(uuid());
            oldCountry.setCreatorId(creator.getId());
            countryRepository.saveAndFlush(oldCountry);
        }
        var newCountry = new Country();
        {
            newCountry.setId(oldCountry.getId());
            newCountry.setName(uuid()); // update
            copyBaseEntityData(oldCountry, newCountry);
            assertNull(newCountry.getUpdatedAt());
            newCountry.setUpdaterId(newCountry.getCreatorId());
            newCountry = countryRepository.saveAndFlush(newCountry);
            assertNotNull(newCountry.getUpdatedAt());
        }
    }

}
