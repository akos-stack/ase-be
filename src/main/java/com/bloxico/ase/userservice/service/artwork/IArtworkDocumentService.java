package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDocumentDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;

import java.util.List;

public interface IArtworkDocumentService {

    List<ArtworkDocumentDto> saveArtworkDocuments(long artworkId, List<DocumentDto> documentDtos);

    void deleteArtworkDocumentById(long artworkId, long documentId);

    void deleteArtworkDocumentsByArtworkId(long artworkId);

    List<Long> findDocumentIdsByArtworkId(long artworkId);

}
