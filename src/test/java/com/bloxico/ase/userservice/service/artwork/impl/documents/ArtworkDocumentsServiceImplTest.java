package com.bloxico.ase.userservice.service.artwork.impl.documents;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilDocument;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.repository.artwork.ArtworkDocumentRepository;
import com.bloxico.ase.userservice.service.artwork.document.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkDocumentsServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilDocument utilDocument;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;
    @Autowired private ArtworkDocumentRepository artworkDocumentRepository;

    @Test
    public void saveArtworkDocuments_nullArtwork() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.saveArtworkDocuments(null, new ArrayList<>()));
    }

    @Test
    public void saveArtworkDocuments_nullList() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.saveArtworkDocuments(1L, null));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void saveArtworkDocuments() {
        var dto = utilArtwork.savedArtworkDtoDraft();
        List<DocumentDto> documentIds = new ArrayList<>();
        for (var category : FileCategory.values()) {
            var documentDto = utilDocument.savedDocumentDto(category);
            documentIds.add(documentDto);
        }
        artworkDocumentService.saveArtworkDocuments(dto.getId(), documentIds);
        var documents = artworkDocumentRepository.findAllById_ArtworkId(dto.getId());
        assertNotNull(documents);
    }

    @Test
    public void removeArtworkDocuments_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.removeArtworkDocument(null));
    }

    @Test
    public void removeArtworkDocuments_nullArtwork() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.removeArtworkDocument(new ArtworkDocument.Id(null, 1L)));
    }

    @Test
    public void removeArtworkDocuments_nullDocument() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.removeArtworkDocument(new ArtworkDocument.Id(1L, null)));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void removeArtworkDocuments() {
        var dto = utilArtwork.savedArtworkDto();
        var document = dto.getDocuments().stream().findFirst().get();
        artworkDocumentService.removeArtworkDocument(new ArtworkDocument.Id(dto.getId(), document.getId()));
        assertTrue(artworkDocumentRepository
                        .findById(new ArtworkDocument.Id(dto.getId(), document.getId())).isEmpty());
    }

    @Test
    public void findDocumentsByArtworkId_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.findDocumentsByArtworkId(null));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void findDocumentsByArtworkId() {
        var dto = utilArtwork.savedArtworkDto();
        var documents = dto.getDocuments().stream().map(BaseEntityDto::getId).collect(Collectors.toList());
        var response = artworkDocumentService.findDocumentsByArtworkId(dto.getId());
        assertNotNull(response);
    }

}
