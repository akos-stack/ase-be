package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContextService;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.facade.IArtworkDocumentsFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.service.artwork.document.IArtworkDocumentService;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_DOCUMENT_ALREADY_ATTACHED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_DOCUMENT_NOT_FOUND;

@Slf4j
@Service
@Transactional
public class ArtworkDocumentsFacadeImpl implements IArtworkDocumentsFacade {

    private final ILocationService locationService;
    private final IDocumentService documentService;
    private final IArtworkService artworkService;
    private final AseSecurityContextService securityContextService;
    private final IArtworkDocumentService artworkDocumentService;

    @Autowired
    public ArtworkDocumentsFacadeImpl(ILocationService locationService, IDocumentService documentService, IArtworkService artworkService, AseSecurityContextService securityContextService, IArtworkDocumentService artworkDocumentService) {
        this.locationService = locationService;
        this.documentService = documentService;
        this.artworkService = artworkService;
        this.securityContextService = securityContextService;
        this.artworkDocumentService = artworkDocumentService;
    }

    @Override
    public SaveArtworkResponse saveArtworkDocuments(SaveArtworkDocumentsRequest request) {
        log.info("ArtworkDocumentFacadeImpl.saveArtworkDocuments - start | request: {}", request);
        request.validateDocuments();
        var artworkDto = artworkService.getArtworkById(request.getArtworkId());
        securityContextService.validateOwner(artworkDto.getOwnerId());
        var documentDtos = documentService.getDocumentsByIds(artworkDocumentService.findDocumentsByArtworkId(request.getArtworkId()));
        var toSaveDocuments = doSaveDocuments(documentDtos, request.getDocuments(), request.getFileCategory());
        artworkDocumentService.saveArtworkDocuments(request.getArtworkId(), toSaveDocuments);
        log.info("ArtworkDocumentFacadeImpl.saveArtworkDocuments - end | request: {}", request);
        return new SaveArtworkResponse(prepareResponse(artworkDto));
    }

    @Override
    public ByteArrayResource downloadArtworkDocument(ArtworkDocumentRequest request) {
        log.info("ArtworkFacadeImpl.downloadArtworkDocument - start | request: {}", request);
        var artworkDto = artworkService.getArtworkById(request.getArtworkId());
        securityContextService.validateOwner(artworkDto.getOwnerId());
        var documents = artworkDocumentService.findDocumentsByArtworkId(request.getArtworkId());
        if(!documents.contains(request.getDocumentId())) {
            throw ARTWORK_DOCUMENT_NOT_FOUND.newException();
        }
        var response = documentService.getDocumentById(request.getDocumentId());
        log.info("ArtworkFacadeImpl.downloadArtworkDocument - start | request: {}", request);
        return response;
    }

    @Override
    public SaveArtworkResponse deleteArtworkDocument(ArtworkDocumentRequest request) {
        log.info("ArtworkFacadeImpl.deleteArtworkDocument - start | request: {}", request);
        var artworkDto = artworkService.getArtworkById(request.getArtworkId());
        securityContextService.validateOwner(artworkDto.getOwnerId());
        var documents = artworkDocumentService.findDocumentsByArtworkId(request.getArtworkId());
        if(!documents.contains(request.getDocumentId())) {
            throw ARTWORK_DOCUMENT_NOT_FOUND.newException();
        }
        artworkDocumentService.removeArtworkDocument(new ArtworkDocument.Id(request.getArtworkId(), request.getDocumentId()));
        documentService.deleteDocumentById(request.getDocumentId());
        log.info("ArtworkFacadeImpl.deleteArtworkDocument - end | request: {}", request);
        return new SaveArtworkResponse(prepareResponse(artworkDto));
    }

    private ArtworkDto prepareResponse(ArtworkDto artworkDto) {
        LocationDto locationDto = null;
        if(artworkDto.getLocation().getId() != null) {
            locationDto = locationService.findLocationById(artworkDto.getLocation().getId());
        }
        List<DocumentDto> documentDtos = documentService.getDocumentsByIds(artworkDocumentService.findDocumentsByArtworkId(artworkDto.getId()));
        return MAPPER.toArtworkDto(artworkDto, locationDto, Set.copyOf(documentDtos));
    }

    private List<DocumentDto> doSaveDocuments(List<DocumentDto> savedDocuments, List<MultipartFile> documents, FileCategory fileCategory) {
        // validation if this artwork already has certificate or resume
        if(FileCategory.CERTIFICATE.equals(fileCategory)
                && hasDocumentByType(savedDocuments, FileCategory.CERTIFICATE)) {
            throw ARTWORK_DOCUMENT_ALREADY_ATTACHED.newException();
        }

        if(FileCategory.CV.equals(fileCategory)
                && hasDocumentByType(savedDocuments, FileCategory.CV)) {
            throw ARTWORK_DOCUMENT_ALREADY_ATTACHED.newException();
        }

        if(FileCategory.PRINCIPAL_IMAGE.equals(fileCategory)
                && hasDocumentByType(savedDocuments, FileCategory.PRINCIPAL_IMAGE)) {
            throw ARTWORK_DOCUMENT_ALREADY_ATTACHED.newException();
        }

        List<DocumentDto> documentDtoList = new ArrayList<>();

        documents.forEach(image -> {
            var documentDto = documentService.saveDocument(image, fileCategory);
            documentDtoList.add(documentDto);
        });
        return documentDtoList;
    }

    private boolean hasDocumentByType(List<DocumentDto> documentDtos, FileCategory fileCategory) {
        return documentDtos.stream().anyMatch(documentDto -> fileCategory.equals(documentDto.getType()));
    }
}
