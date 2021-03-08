package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtistServiceImplTest extends AbstractSpringTest {

    @Autowired private ArtistServiceImpl artistService;
    @Autowired private UtilUser utilUser;
    @Autowired private ArtistRepository artistRepository;

    @Test
    @WithMockCustomUser
    public void saveArtist_artistNull() {
        assertThrows(
                NullPointerException.class,
                () -> artistService.saveArtist(null));
    }

    @Test
    @WithMockCustomUser
    public void saveArtist() {
        var artist = new ArtistDto();
        artist.setName(genUUID());
        artist = artistService.saveArtist(artist);
        assertTrue(artistRepository.findById(artist.getId()).isPresent());
    }
}
