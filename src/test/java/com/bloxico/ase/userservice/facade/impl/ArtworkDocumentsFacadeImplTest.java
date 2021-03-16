package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.UploadArtworkDocumentsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.facade.impl.ArtworkDocumentsFacadeImpl.SINGLETONS;
import static com.bloxico.ase.userservice.util.FileCategory.*;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ArtworkDocumentsFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;
    @Autowired private ArtworkDocumentsFacadeImpl artworkDocumentsFacade;
    @Autowired private UtilSecurityContext utilSecurityContext;

    // TODO ADD downloadArtworkDocument_artworkNotExists

    @Test
    @WithMockCustomUser
    public void downloadArtworkDocument_documentNotExists() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        var request = new ArtworkDocumentRequest(artworkId, -1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.downloadArtworkDocument(request));
    }

    // TODO ADD downloadArtworkDocument_documentNotOfGivenArtwork

    @Test
    @WithMockCustomUser
    public void downloadArtworkDocument() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        assertNotNull(artworkDocumentsFacade.downloadArtworkDocument(
                new ArtworkDocumentRequest(
                        artworkId, document1.getId())));
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_notFound() {
        var request = utilDocument.genUploadArtworkDocumentsRequest(-1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.uploadArtworkDocuments(
                        WithOwner.any(request)));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void uploadArtworkDocuments_notAllowed() {
        var artwork = utilArtwork.saved(utilArtwork.genArtworkDto(Artwork.Status.DRAFT));
        var request = utilDocument.genUploadArtworkDocumentsRequest(artwork.getId());
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.uploadArtworkDocuments(
                        WithOwner.of(utilSecurityContext.getLoggedInArtOwner().getId(), request)));
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_documentNotValid() {
        var request = new UploadArtworkDocumentsRequest(
                utilArtwork.saved(utilArtwork.genArtworkDto()).getId(),
                List.of(genMultipartFile(IMAGE)),
                CERTIFICATE);
        assertThrows(
                S3Exception.class,
                () -> artworkDocumentsFacade.uploadArtworkDocuments(
                        WithOwner.any(request)));
        var ownerId = utilArtwork.ownerIdOf(request.getArtworkId());
        assertThrows(
                S3Exception.class,
                () -> artworkDocumentsFacade.uploadArtworkDocuments(
                        WithOwner.of(ownerId, request)));
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_documentConflict() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        for (var type : SINGLETONS) {
            var request = WithOwner.any(
                    new UploadArtworkDocumentsRequest(
                            artworkId, List.of(genMultipartFile(type)), type));
            assertNotNull(artworkDocumentsFacade.uploadArtworkDocuments(request));
            assertThrows(
                    ArtworkException.class,
                    () -> artworkDocumentsFacade.uploadArtworkDocuments(request));
        }
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_failOnDocumentsSize() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        for (var type : SINGLETONS) {
            var request = WithOwner.any(
                    new UploadArtworkDocumentsRequest(
                            artworkId,
                            List.of(genMultipartFile(type),
                                    genMultipartFile(type)),
                            type));
            assertThrows(
                    ArtworkException.class,
                    () -> artworkDocumentsFacade.uploadArtworkDocuments(request));
        }
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_anyOwner() {
        var request = new UploadArtworkDocumentsRequest(
                utilArtwork.saved(utilArtwork.genArtworkDto()).getId(),
                List.of(genMultipartFile(IMAGE),
                        genMultipartFile(IMAGE)),
                IMAGE);
        var documents = artworkDocumentsFacade
                .uploadArtworkDocuments(WithOwner.any(request))
                .getDocuments();
        assertEquals(
                Set.of(IMAGE),
                documents
                        .stream()
                        .map(DocumentDto::getType)
                        .collect(toSet()));
        assertEquals(
                Set.copyOf(artworkDocumentService
                        .findDocumentIdsByArtworkId(request.getArtworkId())),
                documents.stream().map(DocumentDto::getId).collect(toSet()));
    }

    @Test
    @WithMockCustomUser
    public void uploadArtworkDocuments_ofOwner() {
        var request = new UploadArtworkDocumentsRequest(
                utilArtwork.saved(utilArtwork.genArtworkDto()).getId(),
                List.of(genMultipartFile(IMAGE),
                        genMultipartFile(IMAGE)),
                IMAGE);
        var ownerId = utilArtwork.ownerIdOf(request.getArtworkId());
        var documents = artworkDocumentsFacade
                .uploadArtworkDocuments(WithOwner.of(ownerId, request))
                .getDocuments();
        assertEquals(
                Set.of(IMAGE),
                documents
                        .stream()
                        .map(DocumentDto::getType)
                        .collect(toSet()));
        assertEquals(
                Set.copyOf(artworkDocumentService
                        .findDocumentIdsByArtworkId(request.getArtworkId())),
                documents.stream().map(DocumentDto::getId).collect(toSet()));
    }

    // TODO ADD deleteArtworkDocument_artworkNotFound

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocument_documentNotFound() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var request = new ArtworkDocumentRequest(artworkId, -1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.deleteArtworkDocument(WithOwner.any(request)));
    }

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocument_notAllowed() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document = utilDocument.savedDocumentDto(IMAGE);
        var ownerId = utilUserProfile.savedArtOwnerDto().getId();
        var request = new ArtworkDocumentRequest(artworkId, document.getId());
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.deleteArtworkDocument(
                        WithOwner.of(ownerId, request)));
    }

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocument_anyOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                hasItems(document1.getId(), document2.getId()));
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.any(
                new ArtworkDocumentRequest(artworkId, document1.getId())));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                allOf(hasItems(document2.getId()), not(hasItems(document1.getId()))));
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.any(
                new ArtworkDocumentRequest(artworkId, document2.getId())));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                not(hasItems(document1.getId(), document2.getId())));
    }

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocument_ofOwner() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        var ownerId = utilArtwork.ownerIdOf(artworkId);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                hasItems(document1.getId(), document2.getId()));
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.of(
                ownerId,
                new ArtworkDocumentRequest(artworkId, document1.getId())));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                allOf(hasItems(document2.getId()), not(hasItems(document1.getId()))));
        artworkDocumentsFacade.deleteArtworkDocument(WithOwner.of(
                ownerId,
                new ArtworkDocumentRequest(artworkId, document2.getId())));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                not(hasItems(document1.getId(), document2.getId())));
    }

}
