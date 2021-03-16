package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.config.security.AseSecurityContext;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.FindByArtworkIdRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.*;
import static com.bloxico.ase.userservice.util.FileCategory.CERTIFICATE;
import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private AseSecurityContext securityContext;
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
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
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
    @WithMockCustomUser
    public void updateArtworkData_notAllowed() {
        var request = utilArtwork.genUpdateArtworkDataRequest(DRAFT);
        var ownerId = utilUserProfile.savedArtOwnerDto().getId();
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.updateArtworkData(
                        WithOwner.of(ownerId, request)));
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

//    @Test
//    @WithMockCustomUser
//    public void searchArtworks_ok_owner() {
//        var m1 = utilArtwork.savedArtworkDto();
//        var m2 = utilArtwork.savedArtworkDto();
//        var m3 = utilArtwork.savedArtworkDto();
//        var m4 = utilArtwork.savedArtworkDto(Artwork.Status.WAITING_FOR_EVALUATION);
//        var m5 = utilArtwork.savedArtworkDtoWithOwner();
//        assertThat(
//                artworkFacade
//                        .searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages())
//                        .getPage()
//                        .getContent(),
//                Matchers.allOf(
//                        hasItems(m1, m2, m3),
//                        not(hasItems(m4, m5))));
//    }
//
//    @Test
//    @WithMockCustomUser
//    public void searchArtworks_ok_admin() {
//        var m1 = utilArtwork.savedArtworkDtoWithOwner();
//        var m2 = utilArtwork.savedArtworkDtoWithOwner();
//        var m3 = utilArtwork.savedArtworkDtoWithOwner();
//        var m4 = utilArtwork.savedArtworkDtoWithOwner(WAITING_FOR_EVALUATION);
//        var m5 = utilArtwork.savedArtworkDtoWithOwner();
//        assertThat(
//                artworkFacade.searchArtworks(genSearchArtworksRequest(Artwork.Status.DRAFT), allPages())
//                        .getPage()
//                        .getContent(),
//                Matchers.allOf(
//                        hasItems(m1, m2, m3, m5),
//                        not(hasItems(m4))));
//    }
//
//    @Test
//    @WithMockCustomUser
//    public void deleteArtwork_notAllowed() {
//        var request = utilArtwork.savedArtworkDtoWithOwner();
//        assertThrows(
//                UserException.class,
//                () -> artworkFacade.deleteArtwork(request.getId()));
//    }
//
//    @Test
//    @WithMockCustomUser
//    public void deleteArtwork_notFound() {
//        assertThrows(
//                ArtworkException.class,
//                () -> artworkFacade.deleteArtwork(-1L));
//    }
//
//    @Test
//    @WithMockCustomUser
//    public void deleteArtwork() {
//        var request = utilArtwork.savedArtworkDto();
//        artworkFacade.deleteArtwork(request.getId());
//        assertThrows(
//                ArtworkException.class,
//                () -> artworkFacade.findMyArtworkById(request.getId()));
//        for (var documentDto : request.getDocuments()) {
//            assertThrows(
//                    DocumentException.class,
//                    () -> documentService.findDocumentById(documentDto.getId()));
//        }
//    }

}
