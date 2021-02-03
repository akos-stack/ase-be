package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.util.AseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;

public class ArtworkMetadataServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ArtworkMetadataServiceImpl service;

    @Test
    public void findOrSaveCategory_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), categoryRepository.findAll());
        service.findOrSaveCategory(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
    }

    @Test
    public void findOrSaveCategory_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedCategoryDto();
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
        assertEquals(dto, service.findOrSaveCategory(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
    }

    @Test
    public void fetchCategories() {
        assertEquals(
                List.of(),
                service.fetchCategories(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                List.of(dto),
                service.fetchCategories(null, "", 0, 10, "name").getContent());
    }

    @Test
    public void findOrSaveMaterial_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), materialRepository.findAll());
        service.findOrSaveMaterial(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
    }

    @Test
    public void findOrSaveMaterial_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedMaterialDto();
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
        assertEquals(dto, service.findOrSaveMaterial(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toMaterialEntity(dto)), materialRepository.findAll());
    }

    @Test
    public void fetchMaterials() {
        assertEquals(
                List.of(),
                service.fetchMaterials(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                List.of(dto),
                service.fetchMaterials(null, "", 0, 10, "name").getContent());
    }

    @Test
    public void findOrSaveMedium_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), mediumRepository.findAll());
        service.findOrSaveMedium(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toMediumEntity(dto)), mediumRepository.findAll());
    }

    @Test
    public void findOrSaveMedium_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedMediumDto();
        assertEquals(List.of(AseMapper.MAPPER.toMediumEntity(dto)), mediumRepository.findAll());
        assertEquals(dto, service.findOrSaveMedium(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toMediumEntity(dto)), mediumRepository.findAll());
    }

    @Test
    public void fetchMediums() {
        assertEquals(
                List.of(),
                service.fetchMediums(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedMediumDto();
        assertEquals(
                List.of(dto),
                service.fetchMediums(null, "", 0, 10, "name").getContent());
    }

    @Test
    public void findOrSaveStyle_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), styleRepository.findAll());
        service.findOrSaveStyle(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toStyleEntity(dto)), styleRepository.findAll());
    }

    @Test
    public void findOrSaveStyle_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedStyleDto();
        assertEquals(List.of(AseMapper.MAPPER.toStyleEntity(dto)), styleRepository.findAll());
        assertEquals(dto, service.findOrSaveStyle(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toStyleEntity(dto)), styleRepository.findAll());
    }

    @Test
    public void fetchStyles() {
        assertEquals(
                List.of(),
                service.fetchStyles(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedStyleDto();
        assertEquals(
                List.of(dto),
                service.fetchStyles(null, "", 0, 10, "name").getContent());
    }

}
