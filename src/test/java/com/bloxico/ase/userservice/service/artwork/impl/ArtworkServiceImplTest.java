package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.*;
import static com.bloxico.ase.userservice.entity.user.Role.ART_OWNER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private UtilSecurityContext securityContext;

    @Test
    public void saveArtwork_nullArtwork() {
        assertThrows(
                NullPointerException.class,
                () -> artworkService.saveArtwork(null));
    }

    @Test
    @WithMockCustomUser
    public void saveArtwork() {
        var gen = utilArtwork.genArtworkDto(READY_FOR_EVALUATION);
        var artwork = artworkService.saveArtwork(gen);
        var id = WithOwner.any(artwork.getId());
        assertEquals(artwork, artworkService.findArtworkById(id));
    }

    @Test
    public void findArtworkById_nullWithOwner() {
        assertThrows(
                NullPointerException.class,
                () -> artworkService.findArtworkById(null));
    }

    @Test
    public void findArtworkById_notFound() {
        var id = WithOwner.any(-1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkService.findArtworkById(id));
    }

    @Test
    @WithMockCustomUser
    public void findArtworkById_anyOwner() {
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        assertEquals(artwork1, artworkService.findArtworkById(WithOwner.any(artwork1.getId())));
        assertEquals(artwork2, artworkService.findArtworkById(WithOwner.any(artwork2.getId())));
        assertEquals(artwork3, artworkService.findArtworkById(WithOwner.any(artwork3.getId())));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void findArtworkById_ofOwner() {
        var ownerId = securityContext.getLoggedInArtOwner().getId();
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork4 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION, ownerId));
        var artwork5 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION, ownerId));
        var artwork6 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT, ownerId));
        assertThrows(ArtworkException.class, () -> artworkService.findArtworkById(WithOwner.of(ownerId, artwork1.getId())));
        assertThrows(ArtworkException.class, () -> artworkService.findArtworkById(WithOwner.of(ownerId, artwork2.getId())));
        assertEquals(artwork3, artworkService.findArtworkById(WithOwner.of(ownerId, artwork3.getId())));
        assertEquals(artwork4, artworkService.findArtworkById(WithOwner.of(ownerId, artwork4.getId())));
        assertEquals(artwork5, artworkService.findArtworkById(WithOwner.of(ownerId, artwork5.getId())));
        assertEquals(artwork6, artworkService.findArtworkById(WithOwner.of(ownerId, artwork6.getId())));
    }

    // TODO ADD searchArtworks_nullWithOwner

    // TODO ADD searchArtworks_nullPage

    @Test
    @WithMockCustomUser
    public void searchArtworks_anyOwner() {
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        assertThat(artworkService
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(null, null)),
                                allPages())
                        .getContent(),
                hasItems(artwork1, artwork2, artwork3));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(artwork1.getStatus(), null)),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork1), not(hasItems(artwork2, artwork3))));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(artwork3.getStatus(), artwork3.getTitle())),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2))));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(null, artwork3.getTitle())),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2))));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void searchArtworks_ofOwner() {
        var ownerId = securityContext.getLoggedInArtOwner().getId();
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork4 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION, ownerId));
        var artwork5 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION, ownerId));
        var artwork6 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT, ownerId));
        assertThat(artworkService
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(null, null)),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork3, artwork4, artwork5, artwork6), not(hasItems(artwork1, artwork2))));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(artwork1.getStatus(), null)),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork6), not(hasItems(artwork1, artwork2, artwork3, artwork4, artwork5))));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(artwork3.getStatus(), artwork3.getTitle())),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2, artwork4, artwork5, artwork6))));
        assertThat(
                artworkService
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(null, artwork3.getTitle())),
                                allPages())
                        .getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2, artwork4, artwork5, artwork6))));
    }

    // TODO ADD deleteArtworkById_artworkNotExists

    @Test
    @WithMockCustomUser
    public void deleteArtworkById() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto());
        artworkService.deleteArtworkById(artworkDto.getId());
        assertThrows(
                ArtworkException.class,
                () -> artworkService.findArtworkById(WithOwner.any(artworkDto.getId())));
    }

}
