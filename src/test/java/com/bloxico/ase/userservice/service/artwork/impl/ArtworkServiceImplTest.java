package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private ArtworkRepository artworkRepository;
    @Autowired private ArtworkHistoryRepository artworkHistoryRepository;

    @Test
    @WithMockCustomUser
    public void saveArtwork_nullArtwork() {
        assertThrows(
                NullPointerException.class,
                () -> artworkService.saveArtwork(null));
    }

    @Test
    @WithMockCustomUser
    public void saveArtwork() {
        var dto = utilArtwork.genArtworkDto();
        var newlyCreatedDto = artworkService.saveArtwork(dto);
        assertNotNull(newlyCreatedDto);
        assertTrue(artworkRepository.findById(newlyCreatedDto.getId()).isPresent());
        assertTrue(artworkHistoryRepository.findById(newlyCreatedDto.getId()).isPresent());
    }
}
