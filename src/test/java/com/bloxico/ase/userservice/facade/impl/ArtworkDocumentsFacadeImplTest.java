package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.exception.S3Exception;
import com.bloxico.ase.userservice.exception.UserException;
import com.bloxico.ase.userservice.facade.IArtworkDocumentsFacade;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.userservice.util.FileCategory.CERTIFICATE;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static org.junit.jupiter.api.Assertions.*;

public class ArtworkDocumentsFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private IArtworkDocumentsFacade artworkDocumentsFacade;
    @Autowired private UtilArtwork utilArtwork;

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments_notFound() {
        var request = utilArtwork.genSaveArtworkDocumentsRequest();
        request.setArtworkId(-1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.saveArtworkDocuments(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithOwner();
        var request = new SaveArtworkDocumentsRequest(artworkDto.getId(), List.of(genMultipartFile(IMAGE)), IMAGE);
        assertThrows(
                UserException.class,
                () -> artworkDocumentsFacade.saveArtworkDocuments(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments_documentNotValid() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        var request = new SaveArtworkDocumentsRequest(artworkDto.getId(), List.of(genMultipartFile(IMAGE)), CERTIFICATE);
        assertThrows(
                S3Exception.class,
                () -> artworkDocumentsFacade.saveArtworkDocuments(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments_documentConflict() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        for(var type: FileCategory.values()) {
            if(type == IMAGE) continue;
            var request = new SaveArtworkDocumentsRequest(artworkDto.getId(), List.of(genMultipartFile(type)), type);
            assertThrows(
                    ArtworkException.class,
                    () -> artworkDocumentsFacade.saveArtworkDocuments(request));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments_failOnDocumentsSize() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        for(var type: FileCategory.values()) {
            if(type == IMAGE) continue;
            var request = new SaveArtworkDocumentsRequest(artworkDto.getId(), List.of(genMultipartFile(type), genMultipartFile(type)), type);
            assertThrows(
                    ArtworkException.class,
                    () -> artworkDocumentsFacade.saveArtworkDocuments(request));
        }
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments() {
        var artworkDto = utilArtwork.savedArtworkDtoDraft();
        var documentsSize = 0;
        for(var type: FileCategory.values()) {
            var response = artworkDocumentsFacade.saveArtworkDocuments(new SaveArtworkDocumentsRequest(artworkDto.getId(), List.of(genMultipartFile(type)), type));
            documentsSize++;
            assertNotNull(response);
            assertNotNull(response.getArtworkDto());
            assertEquals(documentsSize, response.getArtworkDto().getDocuments().size());
        }

    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void downloadArtworkDocument_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithOwner();
        var request = new ArtworkDocumentRequest(artworkDto.getId(), 1L);
        assertThrows(
                UserException.class,
                () -> artworkDocumentsFacade.downloadArtworkDocument(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void downloadArtworkDocument_artworkDocumentNotFound() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        var request = new ArtworkDocumentRequest(artworkDto.getId(), -1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.downloadArtworkDocument(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void downloadArtworkDocument() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        var documentDto = artworkDto.getDocuments().stream().findFirst().get();
        var response = artworkDocumentsFacade.downloadArtworkDocument(
                new ArtworkDocumentRequest(artworkDto.getId(), documentDto.getId()));
        assertNotNull(response);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void removeArtworkDocument_notAllowed() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithOwner();
        var request = new ArtworkDocumentRequest(artworkDto.getId(), 1L);
        assertThrows(
                UserException.class,
                () -> artworkDocumentsFacade.downloadArtworkDocument(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void removeArtworkDocument_notFound() {
        var artworkDto = utilArtwork.savedArtworkDto();
        var request = new ArtworkDocumentRequest(artworkDto.getId(), -1L);
        assertThrows(
                ArtworkException.class,
                () -> artworkDocumentsFacade.downloadArtworkDocument(request));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void removeArtworkDocument() {
        var artworkDto = utilArtwork.savedArtworkDtoDraftWithDocuments();
        var documentDto = artworkDto.getDocuments().stream().findFirst().get();
        var request = new ArtworkDocumentRequest(artworkDto.getId(), documentDto.getId());
        var response = artworkDocumentsFacade.deleteArtworkDocument(request);
        assertNotNull(response);
        assertNotNull(response.getArtworkDto());
        assertNotNull(response.getArtworkDto().getDocuments());
        assertTrue(response.getArtworkDto().getDocuments().stream().noneMatch(documentDto1 -> documentDto1.getId().equals(documentDto.getId())));
    }
}
