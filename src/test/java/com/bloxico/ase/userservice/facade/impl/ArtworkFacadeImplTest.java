package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilSecurityContext;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.exception.DocumentException;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.testutil.UtilArtwork.genSearchArtworksRequest;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private IDocumentService documentService;

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void previewArtwork_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithOwner();
        assertThrows(
                UserException.class,
                () -> artworkFacade.getArtworkById(artworkDto.getId()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void previewArtwork_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.getArtworkById(-1L));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void previewArtwork() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        var response = artworkFacade.getArtworkById(artworkDto.getId());
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
        assertEquals(artworkDto, response.getArtworkDto());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDraft() {
        var response = artworkFacade.saveArtworkDraft();
        assertNotNull(response);
        assertNotNull(response.getArtworkDto().getId());
        assertSame(Artwork.Status.DRAFT, response.getArtworkDto().getStatus());
        assertSame(utilSecurityContext.getLoggedInArtOwner().getId(), response.getArtworkDto().getOwnerId());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkData_notAllowed() {
        var request = utilArtwork.genSaveArtworkDataRequestWithOwner(Artwork.Status.DRAFT, true);
        assertThrows(
                UserException.class,
                () -> artworkFacade.saveArtworkData(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkData_artworkNotFound() {
        var artworkDto = new ArtworkDto();
        artworkDto.setId(-1L);
        var request = utilArtwork.genSaveArtworkDataRequestWithDocuments(artworkDto, Artwork.Status.DRAFT, false);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.saveArtworkData(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkData_missingCertificate() {
        var request = utilArtwork.genSaveArtworkDataRequest(Artwork.Status.WAITING_FOR_EVALUATION, false);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.saveArtworkData(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkData_missingResume() {
        var request = utilArtwork.genSaveArtworkDataRequest(Artwork.Status.WAITING_FOR_EVALUATION, true);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.saveArtworkData(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkData() {
        var request = utilArtwork.genSaveArtworkDataRequestWithDocuments(Artwork.Status.DRAFT, true);
        var response = artworkFacade.saveArtworkData(request);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
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
                artworkFacade.searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages()).getPage().getContent(),
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
                artworkFacade.searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages()).getPage().getContent(),
                Matchers.allOf(
                        hasItems(m1, m2, m3, m5),
                        not(hasItems(m4))));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void deleteArtwork_notAllowed() {
        var request = utilArtwork.savedArtworkDtoDraftWithOwner();
        assertThrows(
                UserException.class,
                () -> artworkFacade.deleteArtwork(request.getId()));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void deleteArtwork_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.deleteArtwork(-1L));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void deleteArtwork() {
        var request = utilArtwork.savedArtworkDto();
        artworkFacade.deleteArtwork(request.getId());
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.getArtworkById(request.getId()));
        for (var documentDto: request.getDocuments()) {
            assertThrows(
                    DocumentException.class,
                    () -> documentService.getDocumentById(documentDto.getId()));
        }
    }

}
