package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.address.Region;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertTrue;

public class RegionRepositoryTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private RegionRepository repository;

    @Test
    public void saveAndFindById() {
        assertTrue(repository.findById(-1).isEmpty());
        var creatorId = mockUtil.savedAdmin().getId();
        var region = new Region();
        region.setName(uuid());
        region.setCreatorId(creatorId);
        var id = repository.saveAndFlush(region).getId();
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    public void findByNameIgnoreCase() {
        assertTrue(repository.findByNameIgnoreCase(uuid()).isEmpty());
        var region = mockUtil.savedRegion();
        assertTrue(repository.findByNameIgnoreCase(region.getName()).isPresent());
    }

}
