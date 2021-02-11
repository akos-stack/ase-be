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
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
