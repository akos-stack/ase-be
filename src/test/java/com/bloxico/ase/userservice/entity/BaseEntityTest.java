package com.bloxico.ase.userservice.entity;

import com.bloxico.ase.WithMockCustomUser;
import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.repository.address.CountryRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static com.bloxico.ase.testutil.Util.copyBaseEntityData;
import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseEntityTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private CountryRepository countryRepository;

    @Test
    public void prePersist_creator_isNull() {
        var country = new Country();
        country.setName(genUUID());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> countryRepository.saveAndFlush(country));
    }

    @Test
    @WithMockCustomUser
    public void prePersist_creator_isNotNull() {
        var country = new Country();
        country.setName(genUUID());
        assertNull(country.getCreatedAt());
        country = countryRepository.saveAndFlush(country);
        assertNotNull(country.getCreatedAt());
    }

    @Test
    @WithMockCustomUser
    public void preUpdate_updater_isNotNull() {
        var oldCountry = new Country();
        {
            oldCountry.setName(genUUID());
            countryRepository.saveAndFlush(oldCountry);
        }
        var newCountry = new Country();
        {
            newCountry.setId(oldCountry.getId());
            newCountry.setName(genUUID()); // update
            copyBaseEntityData(oldCountry, newCountry);
            assertNull(newCountry.getUpdatedAt());
            newCountry.setUpdaterId(newCountry.getCreatorId());
            newCountry = countryRepository.saveAndFlush(newCountry);
            assertNotNull(newCountry.getUpdatedAt());
        }
    }

}
