package com.bloxico.ase.userservice.service.artwork.document;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;

import java.util.List;

public interface IArtworkDocumentService {

    void saveArtworkDocuments(Long artworkId, List<DocumentDto> documentDtos);

    void removeArtworkDocument(ArtworkDocument.Id id);

    List<Long> findDocumentsByArtworkId(Long artworkId);
}
