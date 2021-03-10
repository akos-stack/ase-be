package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.repository.document.DocumentRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkServiceImplTest extends AbstractSpringTestWithAWS {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private ArtworkRepository artworkRepository;
    @Autowired private ArtworkHistoryRepository artworkHistoryRepository;
    @Autowired private DocumentRepository documentRepository;

    @Test
    public void saveArtwork_nullArtwork() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> artworkService.saveArtwork(null, principalId));
    }

    @Test
    public void saveArtwork() {
        var principalId = utilUser.savedAdmin().getId();
        var dto = utilArtwork.genArtworkDto();
        var newlyCreatedDto = artworkService.saveArtwork(dto, principalId);
        assertNotNull(newlyCreatedDto);
        assertTrue(artworkRepository.findById(newlyCreatedDto.getId()).isPresent());
        assertTrue(artworkHistoryRepository.findById(newlyCreatedDto.getId()).isPresent());

        var artwork = artworkRepository.findById(newlyCreatedDto.getId());
        var listOfDocuments = artwork.get().getDocuments();
        for (Document dok: listOfDocuments) {
            var foundDokument = documentRepository.findById(dok.getId());
            assertNotNull(amazonS3.getObject(bucketName, foundDokument.get().getPath()));
        }
    }
}
