package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.util.AseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;

public class ArtworkMaterialServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private MaterialServiceImpl service;

    @Autowired
    private MaterialRepository materialRepository;

    @Test
    public void findOrSaveMaterial_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), materialRepository.findAll());
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
    }

    @Test
    public void findOrSaveMaterial_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedMaterialDto();
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
    }

    @Test
    public void fetchMaterials() {
        assertEquals(
                List.of(),
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                List.of(dto),
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent());
    }
}
