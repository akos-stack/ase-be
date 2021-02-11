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
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
