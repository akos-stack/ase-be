package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.util.AseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ArtworkMaterialServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private MaterialServiceImpl service;
    @Autowired private MaterialRepository materialRepository;

    @Test
    public void findOrSaveMaterial_notFound() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(genUUID());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertThat(materialRepository.findAll(), not(hasItems(AseMapper.MAPPER.toMaterialEntity(dto))));
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertThat(materialRepository.findAll(), hasItems(AseMapper.MAPPER.toMaterialEntity(dto)));
    }

    @Test
    public void findOrSaveMaterial_found() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedMaterialDto();
        assertThat(materialRepository.findAll(), hasItems(AseMapper.MAPPER.toMaterialEntity(dto)));
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
    }

    @Test
    public void fetchMaterials() {
        var dto = utilArtworkMetadata.savedMaterialDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedMaterialDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto, dto2));
    }
}
