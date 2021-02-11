package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.util.AseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ArtworkCategoryServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private CategoryServiceImpl service;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    public void findOrSaveCategory_saved() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(genUUID());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertThat(categoryRepository.findAll(), not(hasItems(AseMapper.MAPPER.toCategoryEntity(dto))));
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertThat(categoryRepository.findAll(), hasItems(AseMapper.MAPPER.toCategoryEntity(dto)));
    }

    @Test
    public void findOrSaveCategory_notSaved() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedCategoryDto();
        assertThat(categoryRepository.findAll(), hasItems(AseMapper.MAPPER.toCategoryEntity(dto)));
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
    }

    @Test
    public void fetchCategories() {
        var dto = utilArtworkMetadata.savedCategoryDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedCategoryDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto, dto2));
    }

}
