package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.repository.artwork.ArtworkDocumentRepository;
import com.bloxico.ase.userservice.util.FileCategory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.bloxico.ase.userservice.util.FileCategory.CV;
import static com.bloxico.ase.userservice.util.FileCategory.IMAGE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkDocumentsServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilDocument utilDocument;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;
    @Autowired private ArtworkDocumentRepository artworkDocumentRepository;

    @Test
    public void saveArtworkDocuments_nullDocuments() {
        assertThrows(
                NullPointerException.class,
                () -> artworkDocumentService.saveArtworkDocuments(1L, null));
    }

    // TODO ADD saveArtworkDocuments_emptyDocuments

    // TODO ADD saveArtworkDocuments_artworkNotExists

    @Test
    @WithMockCustomUser
    public void saveArtworkDocuments() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var documents = Arrays
                .stream(FileCategory.values())
                .map(utilDocument::savedDocumentDto)
                .collect(toList());
        artworkDocumentService.saveArtworkDocuments(artworkId, documents);
        assertEquals(
                documents
                        .stream()
                        .map(DocumentDto::getId)
                        .collect(toSet()),
                artworkDocumentRepository
                        .findAllByIdArtworkId(artworkId)
                        .stream()
                        .map(ArtworkDocument::getId)
                        .map(ArtworkDocument.Id::getDocumentId)
                        .collect(toSet()));
    }

    // TODO ADD deleteArtworkDocumentById_artworkNotExists

    // TODO ADD deleteArtworkDocumentById_documentNotExists

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocumentById() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                hasItems(document1.getId(), document2.getId()));
        artworkDocumentService.deleteArtworkDocumentById(artworkId, document1.getId());
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                allOf(
                        hasItems(document2.getId()),
                        not(hasItems(document1.getId()))));
    }

    // TODO ADD deleteArtworkDocumentsByArtworkId_artworkNotExists

    @Test
    @WithMockCustomUser
    public void deleteArtworkDocumentsByArtworkId() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document1 = utilDocument.savedDocumentDto(IMAGE);
        var document2 = utilDocument.savedDocumentDto(CV);
        artworkDocumentService.saveArtworkDocuments(
                artworkId, List.of(document1, document2));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                hasItems(document1.getId(), document2.getId()));
        artworkDocumentService.deleteArtworkDocumentsByArtworkId(artworkId);
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                not(hasItems(document1.getId(), document2.getId())));
    }

    // TODO ADD findDocumentIdsByArtworkId_artworkNotExists

    @Test
    @WithMockCustomUser
    public void findDocumentIdsByArtworkId() {
        var artworkId = utilArtwork.saved(utilArtwork.genArtworkDto()).getId();
        var document = utilDocument.savedDocumentDto(IMAGE);
        artworkDocumentService.saveArtworkDocuments(artworkId, List.of(document));
        assertThat(
                artworkDocumentService.findDocumentIdsByArtworkId(artworkId),
                hasItems(document.getId()));
    }

}
