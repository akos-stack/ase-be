package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDocumentDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument.Id;
import com.bloxico.ase.userservice.repository.artwork.ArtworkDocumentRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ArtworkDocumentServiceImpl implements IArtworkDocumentService {

    private final ArtworkDocumentRepository artworkDocumentRepository;

    @Autowired
    public ArtworkDocumentServiceImpl(ArtworkDocumentRepository artworkDocumentRepository) {
        this.artworkDocumentRepository = artworkDocumentRepository;
    }

    @Override
    public List<ArtworkDocumentDto> saveArtworkDocuments(long artworkId, List<DocumentDto> documentDtos) {
        log.info("ArtworkDocumentServiceImpl.saveArtworkDocuments - start | artworkId: {}, documents: {} ", artworkId, documentDtos);
        requireNonNull(documentDtos);
        var artworkDocuments = documentDtos
                .stream()
                .map(documentDto -> new Id(artworkId, documentDto.getId()))
                .map(ArtworkDocument::new)
                .map(artworkDocumentRepository::saveAndFlush)
                .map(MAPPER::toDto)
                .collect(toList());
        log.info("ArtworkDocumentServiceImpl.saveArtworkDocuments - end | artworkId: {}, documents: {} ", artworkId, documentDtos);
        return artworkDocuments;
    }

    @Override
    public void deleteArtworkDocumentById(long artworkId, long documentId) {
        log.info("ArtworkDocumentServiceImpl.deleteArtworkDocumentById - start | artworkId: {}, documentId: {}", artworkId, documentId);
        artworkDocumentRepository.deleteById(new Id(artworkId, documentId));
        log.info("ArtworkDocumentServiceImpl.deleteArtworkDocumentById - end | artworkId: {}, documentId: {}", artworkId, documentId);
    }

    @Override
    public void deleteArtworkDocumentsByArtworkId(long id) {
        log.info("ArtworkDocumentServiceImpl.deleteArtworkDocumentsByArtworkId - start | id: {}", id);
        artworkDocumentRepository.deleteByIdArtworkId(id);
        log.info("ArtworkDocumentServiceImpl.deleteArtworkDocumentsByArtworkId - end | id: {}", id);
    }

    @Override
    public List<Long> findDocumentIdsByArtworkId(long artworkId) {
        log.info("ArtworkDocumentServiceImpl.findDocumentsByArtworkId - start | artworkId: {}", artworkId);
        var documentIds = artworkDocumentRepository
                .findAllByIdArtworkId(artworkId)
                .stream()
                .map(document -> document.getId().getDocumentId())
                .collect(toList());
        log.info("ArtworkDocumentServiceImpl.findDocumentsByArtworkId - end | artworkId: {}", artworkId);
        return documentIds;
    }

}
