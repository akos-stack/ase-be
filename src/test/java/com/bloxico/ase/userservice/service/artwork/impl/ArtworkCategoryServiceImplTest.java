package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.util.AseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;

public class ArtworkCategoryServiceImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private CategoryServiceImpl service;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void findOrSaveCategory_notFound() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(uuid());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertEquals(List.of(), categoryRepository.findAll());
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
    }

    @Test
    public void findOrSaveCategory_found() {
        var principalId = mockUtil.savedAdmin().getId();
        var dto = mockUtil.savedCategoryDto();
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
        assertEquals(List.of(AseMapper.MAPPER.toCategoryEntity(dto)), categoryRepository.findAll());
    }

    @Test
    public void fetchCategories() {
        assertEquals(
                List.of(),
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent());
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                List.of(dto),
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent());
    }

}
