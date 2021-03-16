package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.config.security.AseSecurityContext;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.DeleteArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.FindByArtworkIdRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.Util.allPages;
import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.*;
import static com.bloxico.ase.userservice.entity.user.Role.ART_OWNER;
import static com.bloxico.ase.userservice.util.FileCategory.CERTIFICATE;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private AseSecurityContext securityContext;
    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private IDocumentService documentService;
    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDraft() {
        var response = artworkFacade.createArtworkDraft();
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getArtwork().getId());
        assertSame(DRAFT, response.getArtwork().getStatus());
        assertEquals(
                securityContext.getArtOwnerId(),
                response.getArtwork().getOwnerId().longValue());
    }

    @Test
    @WithMockCustomUser
    public void findArtworkById_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.findArtworkById(WithOwner.any(
                        new FindByArtworkIdRequest(-1L))));
    }

    @Test
    @WithMockCustomUser
    public void findArtworkById_notAllowed() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT)).getId();
        var ownerId = utilUserProfile.savedArtOwnerDto().getId();
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.findArtworkById(WithOwner.of(
                        ownerId,
                        new FindByArtworkIdRequest(artworkId))));
    }

    @Test
    @WithMockCustomUser
    public void findArtworkById_anyOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var response = artworkFacade.findArtworkById(WithOwner.any(
                new FindByArtworkIdRequest(artworkId)));
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
        assertNotNull(response.getLocation());
    }

    @Test
    @WithMockCustomUser
    public void findArtworkById_ofOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var response = artworkFacade.findArtworkById(WithOwner.of(
                utilArtwork.ownerIdOf(artworkId),
                new FindByArtworkIdRequest(artworkId)));
        assertNotNull(response);
        assertNotNull(response.getArtwork());
        assertNotNull(response.getDocuments());
        assertNotNull(response.getLocation());
    }

    // TODO ADD findArtworkById_withDocumentsAndLocation

    @Test
    @WithMockCustomUser
    public void updateArtworkData_artworkNotFound() {
        var request = utilArtwork.genUpdateArtworkDataRequest(-1L, DRAFT);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(
                        WithOwner.any(request)));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void updateArtworkData_notAllowed() {
        var artworkDto = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkDto.getId(), DRAFT);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(
                        WithOwner.of(utilSecurityContext.getLoggedInArtOwner().getId(), request)));
    }

    @Test
    @WithMockCustomUser
    public void updateArtworkData_missingCertificate() {
        var resumeId = utilDocument.savedDocumentDto(CV);
        var request = utilArtwork.genUpdateArtworkDataRequest(WAITING_FOR_EVALUATION, false);
        var artworkId = request.getArtworkId();
        artworkDocumentService.saveArtworkDocuments(artworkId, List.of(resumeId));
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(WithOwner.any(request)));
    }

    @Test
    @WithMockCustomUser
    public void updateArtworkData_missingResume() {
        var certificateId = utilDocument.savedDocumentDto(CERTIFICATE);
        var request = utilArtwork.genUpdateArtworkDataRequest(WAITING_FOR_EVALUATION, true);
        var artworkId = request.getArtworkId();
        artworkDocumentService.saveArtworkDocuments(artworkId, List.of(certificateId));
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(WithOwner.any(request)));
    }

    @Test
    @WithMockCustomUser
    public void updateArtworkData_anyOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT)).getId();
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, READY_FOR_EVALUATION);
        var resumeId = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(artworkId, List.of(resumeId));
        var response = artworkFacade.updateArtworkData(WithOwner.any(request));
        assertEquals(artworkId, response.getArtwork().getId());
        // TODO ADD more assertions
    }

    @Test
    @WithMockCustomUser
    public void updateArtworkData_ofOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT)).getId();
        var ownerId = utilArtwork.ownerIdOf(artworkId);
        var request = utilArtwork.genUpdateArtworkDataRequest(artworkId, READY_FOR_EVALUATION);
        var resumeId = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(artworkId, List.of(resumeId));
        var response = artworkFacade.updateArtworkData(WithOwner.of(ownerId, request));
        assertEquals(artworkId, response.getArtwork().getId());
        // TODO ADD more assertions
    }

    @Test
    @WithMockCustomUser
    public void searchArtworks_anyOwner() {
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        assertThat(artworkFacade
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(null, null)),
                                allPages())
                        .getPage().getContent(),
                hasItems(artwork1, artwork2, artwork3));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(artwork1.getStatus(), null)),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork1), not(hasItems(artwork2, artwork3))));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(artwork3.getStatus(), artwork3.getTitle())),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2))));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.any(new SearchArtworkRequest(null, artwork3.getTitle())),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2))));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void searchArtworks_ofOwner() {
        var ownerId = utilSecurityContext.getLoggedInArtOwner().getId();
        var artwork1 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var artwork2 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION));
        var artwork3 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION));
        var artwork4 = utilArtwork.saved(utilArtwork.genArtworkDto(WAITING_FOR_EVALUATION, ownerId));
        var artwork5 = utilArtwork.saved(utilArtwork.genArtworkDto(READY_FOR_EVALUATION, ownerId));
        var artwork6 = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT, ownerId));
        assertThat(artworkFacade
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(null, null)),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork3, artwork4, artwork5, artwork6), not(hasItems(artwork1, artwork2))));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(artwork1.getStatus(), null)),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork6), not(hasItems(artwork1, artwork2, artwork3, artwork4, artwork5))));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(artwork3.getStatus(), artwork3.getTitle())),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2, artwork4, artwork5, artwork6))));
        assertThat(
                artworkFacade
                        .searchArtworks(
                                WithOwner.of(ownerId, new SearchArtworkRequest(null, artwork3.getTitle())),
                                allPages())
                        .getPage().getContent(),
                allOf(hasItems(artwork3), not(hasItems(artwork1, artwork2, artwork4, artwork5, artwork6))));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void deleteArtwork_notAllowed() {
        var artwork = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT));
        var request = utilArtwork.genUpdateArtworkDataRequest(artwork.getId(), DRAFT);
        var ownerId = utilUserProfile.savedArtOwnerDto().getId();
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(
                        WithOwner.of(ownerId, request)));
    }

    @Test
    @WithMockCustomUser
    public void deleteArtwork_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.deleteArtwork(WithOwner.any(
                        new DeleteArtworkRequest(-1L))));
    }

    @Test
    @WithMockCustomUser(role = ART_OWNER)
    public void deleteArtwork() {
        var request = utilArtwork.saved(utilArtwork.genArtworkDto(DRAFT, securityContext.getArtOwnerId()));
        artworkFacade.deleteArtwork(WithOwner.of(securityContext.getArtOwnerId(), new DeleteArtworkRequest(request.getId())));
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.findArtworkById(WithOwner.any(new FindByArtworkIdRequest(request.getId()))));
        var documents = documentService.findDocumentsByArtworkId(request.getId());
        assertTrue(documents.isEmpty());
    }

}
