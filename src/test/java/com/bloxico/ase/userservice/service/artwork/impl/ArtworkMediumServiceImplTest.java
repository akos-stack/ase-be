package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.PENDING;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ArtworkMediumServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private MediumServiceImpl service;
    @Autowired private MediumRepository mediumRepository;

    @Test
    public void findOrSaveMedium_notFound() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = new ArtworkMetadataDto();
        dto.setName(genUUID());
        dto.setStatus(ArtworkMetadataStatus.APPROVED);
        assertThat(mediumRepository.findAll(), not(hasItems(MAPPER.toMediumEntity(dto))));
        service.findOrSaveArtworkMetadata(dto, principalId);
        assertThat(mediumRepository.findAll(), hasItems(MAPPER.toMediumEntity(dto)));
    }

    @Test
    public void findOrSaveMedium_found() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedMediumDto();
        assertThat(mediumRepository.findAll(), hasItems(MAPPER.toMediumEntity(dto)));
        assertEquals(dto, service.findOrSaveArtworkMetadata(dto, principalId));
    }

    @Test
    public void updateMediumStatus() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtworkMetadata.savedMediumDto();

        assertTrue(mediumRepository.findByNameIgnoreCase(dto.getName()).get().getStatus() == APPROVED);

        dto.setStatus(PENDING);
        service.updateArtworkMetadataStatus(dto, principalId);

        assertTrue(mediumRepository.findByNameIgnoreCase(dto.getName()).get().getStatus() == PENDING);
    }

    @Test
    public void deleteMedium() {
        var dto = utilArtworkMetadata.savedMediumDto();
        assertNotNull(mediumRepository.findByNameIgnoreCase(dto.getName()));

        service.deleteArtworkMetadata(dto.getName());
        assertTrue(mediumRepository.findByNameIgnoreCase(dto.getName()).isEmpty());
    }

    @Test
    public void fetchApprovedMediums() {
        var dto = utilArtworkMetadata.savedMediumDto();
        var dto2 = utilArtworkMetadata.savedMediumDto(PENDING);
        assertThat(
                service.searchApprovedArtworkMetadata(""),
                allOf(hasItems(dto), not(hasItems(dto2))));
    }

    @Test
    public void fetchMediums() {
        var dto = utilArtworkMetadata.savedMediumDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedMediumDto();
        assertThat(
                service.searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(dto, dto2));
    }
}
