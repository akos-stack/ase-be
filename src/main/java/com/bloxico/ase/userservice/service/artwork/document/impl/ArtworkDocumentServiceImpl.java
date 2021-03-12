package com.bloxico.ase.userservice.service.artwork.document.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDocumentDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.repository.artwork.ArtworkDocumentRepository;
import com.bloxico.ase.userservice.service.artwork.document.IArtworkDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkDocumentServiceImpl implements IArtworkDocumentService {

    private final ArtworkDocumentRepository artworkDocumentRepository;

    public ArtworkDocumentServiceImpl(ArtworkDocumentRepository artworkDocumentRepository) {
        this.artworkDocumentRepository = artworkDocumentRepository;
    }

    @Override
    public List<ArtworkDocumentDto> saveArtworkDocuments(Long artworkId, List<DocumentDto> documentDtos) {
        log.info("ArtworkDocumentServiceImpl.saveArtworkDocument - start | artworkId: {}, documents: {} ", artworkId, documentDtos);
        requireNonNull(artworkId);
        requireNonNull(documentDtos);
        var artworkDocumentList = documentDtos.stream().map(documentDto -> {
            var artworkDocument = new ArtworkDocument();
            var artworkDocumentID = new ArtworkDocument.Id();
            artworkDocumentID.setDocumentId(documentDto.getId());
            artworkDocumentID.setArtworkId(artworkId);
            artworkDocument.setId(artworkDocumentID);
            return artworkDocument;
        }).collect(Collectors.toList());
        artworkDocumentList = artworkDocumentRepository.saveAll(artworkDocumentList);
        log.info("ArtworkDocumentServiceImpl.saveArtworkDocument - start | artworkId: {}, documents: {} ", artworkId, documentDtos);
        return artworkDocumentList.stream().map(MAPPER::toDto).collect(Collectors.toList());
    }

    @Override
    public void removeArtworkDocument(ArtworkDocument.Id id) {
        log.info("ArtworkDocumentServiceImpl.removeArtworkDocument - start | id: {}", id);
        requireNonNull(id);
        requireNonNull(id.getDocumentId());
        requireNonNull(id.getArtworkId());
        artworkDocumentRepository.deleteById(id);
        log.info("ArtworkDocumentServiceImpl.removeArtworkDocument - end | id: {}", id);
    }

    @Override
    public List<Long> findDocumentsByArtworkId(Long artworkId) {
        log.info("ArtworkDocumentServiceImpl.findDocumentsByArtworkId - start | artworkId: {}", artworkId);
        requireNonNull(artworkId);
        var documentIds = artworkDocumentRepository.findAllById_ArtworkId(artworkId).stream().map(artworkDocument -> artworkDocument.getId().getDocumentId()).collect(Collectors.toList());
        log.info("ArtworkDocumentServiceImpl.findDocumentsByArtworkId - end | artworkId: {}", artworkId);
        return documentIds;
    }
}
