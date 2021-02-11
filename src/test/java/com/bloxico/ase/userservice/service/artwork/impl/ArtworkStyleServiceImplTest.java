package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.PENDING;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ArtworkStyleServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private StyleServiceImpl service;
    @Autowired private StyleRepository styleRepository;

    @Test
    public void findOrSaveStyle_notFound() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(genUUID());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertThat(styleRepository.findAll(), not(hasItems(MAPPER.toStyleEntity(dto))));
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertThat(styleRepository.findAll(), hasItems(MAPPER.toStyleEntity(dto)));
    }

    @Test
    public void findOrSaveStyle_found() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedStyleDto();
        assertThat(styleRepository.findAll(), hasItems(MAPPER.toStyleEntity(dto)));
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
    }

    @Test
    public void updateStyleStatus() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedStyleDto();

        assertTrue(styleRepository.findByNameIgnoreCase(dto.getName()).get().getStatus() == APPROVED);

        dto.setStatus(PENDING);
        service.updateArtworkMetadataStatus(dto, principalId);

        assertTrue(styleRepository.findByNameIgnoreCase(dto.getName()).get().getStatus() == PENDING);
    }

    @Test
    public void deleteStyle() {
        var dto = utilArtworkMetadata.savedStyleDto();
        assertNotNull(styleRepository.findByNameIgnoreCase(dto.getName()));

        service.deleteArtworkMetadata(dto.getName());
        assertTrue(styleRepository.findByNameIgnoreCase(dto.getName()).isEmpty());
    }

    @Test
    public void fetchApprovedStyles() {
        var dto = utilArtworkMetadata.savedStyleDto();
        var dto2 = utilArtworkMetadata.savedStyleDto(PENDING);
        assertThat(
                service.searchApprovedArtworkMetadata(""),
                allOf(hasItems(dto), not(hasItems(dto2))));
    }

    @Test
    public void fetchStyles() {
        var dto = utilArtworkMetadata.savedStyleDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedStyleDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto, dto2));
    }
}
