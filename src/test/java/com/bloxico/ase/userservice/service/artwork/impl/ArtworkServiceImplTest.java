package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.testutil.UtilArtwork.genSearchArtworksRequest;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private ArtworkRepository artworkRepository;

    @Test
    public void saveArtwork_nullArtwork() {
        assertThrows(
                NullPointerException.class,
                () -> artworkService.saveArtwork(null));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtwork() {
        var dto = utilArtwork.genArtworkDto(Artwork.Status.DRAFT);
        var newlyCreatedDto = artworkService.saveArtwork(dto);
        assertNotNull(newlyCreatedDto);
        assertTrue(artworkRepository.findById(newlyCreatedDto.getId()).isPresent());
    }

    @Test
    public void getArtworkById_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> artworkService.getArtworkById(null));
    }

    @Test
    public void getArtworkById_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkService.getArtworkById(-1L));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void getArtworkById() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        var response = artworkService.getArtworkById(artworkDto.getId());
        assertNotNull(response);
        assertEquals(artworkDto, response);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void searchArtworks_ok_owner() {
        var m1 = utilArtwork.savedArtworkDto();
        var m2 = utilArtwork.savedArtworkDto();
        var m3 = utilArtwork.savedArtworkDto();
        var m4 = utilArtwork.savedArtworkDto(Artwork.Status.WAITING_FOR_EVALUATION);
        var m5 = utilArtwork.savedArtworkDtoDraftWithOwner();
        assertThat(
                artworkService.searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages()).getContent(),
                Matchers.allOf(
                        hasItems(m1, m2, m3),
                        not(hasItems(m4, m5))));
    }

    @Test
    @WithMockCustomUser
    public void searchArtworks_ok_admin() {
        var m1 = utilArtwork.savedArtworkDtoDraftWithOwner();
        var m2 = utilArtwork.savedArtworkDtoDraftWithOwner();
        var m3 = utilArtwork.savedArtworkDtoDraftWithOwner();
        var m4 = utilArtwork.savedArtworkDtoDraftWithOwner(Artwork.Status.WAITING_FOR_EVALUATION);
        var m5 = utilArtwork.savedArtworkDtoDraftWithOwner();
        assertThat(
                artworkService.searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages()).getContent(),
                Matchers.allOf(
                        hasItems(m1, m2, m3, m5),
                        not(hasItems(m4))));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void deleteArtworkById() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        artworkService.deleteArtworkById(artworkDto.getId());
        assertThrows(
                ArtworkException.class,
                () -> artworkService.getArtworkById(artworkDto.getId()));
    }
}
