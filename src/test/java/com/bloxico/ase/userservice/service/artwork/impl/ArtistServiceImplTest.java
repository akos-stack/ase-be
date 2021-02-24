package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.artwork.Artist;
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
    public void saveArtist_artistNull() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> artistService.saveArtist(null, principalId));
    }

    @Test
    public void saveArtist() {
        var principalId = utilUser.savedAdmin().getId();
        var artist = new Artist();
        artist.setName(genUUID());
        artist.setCreatorId(principalId);
        artist = artistRepository.save(artist);
        assertTrue(artistRepository.findById(artist.getId()).isPresent());
    }
}
