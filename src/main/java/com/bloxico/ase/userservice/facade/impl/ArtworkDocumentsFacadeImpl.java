package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.facade.IArtworkDocumentsFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkDocumentService;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.UploadArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.UploadArtworkDocumentsResponse;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SetArtworkPrincipalImageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.bloxico.ase.userservice.util.FileCategory.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.*;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class ArtworkDocumentsFacadeImpl implements IArtworkDocumentsFacade {

    public static final Set<FileCategory> SINGLETONS = Set.of(CERTIFICATE, CV, PRINCIPAL_IMAGE);

    private final IDocumentService documentService;
    private final IArtworkService artworkService;
    private final IArtworkDocumentService artworkDocumentService;

    @Autowired
    public ArtworkDocumentsFacadeImpl(IDocumentService documentService,
                                      IArtworkService artworkService,
                                      IArtworkDocumentService artworkDocumentService)
    {
        this.documentService = documentService;
        this.artworkService = artworkService;
        this.artworkDocumentService = artworkDocumentService;
    }

    @Override
    public ByteArrayResource downloadArtworkDocument(ArtworkDocumentRequest request) {
        log.info("ArtworkFacadeImpl.downloadArtworkDocument - start | request: {}", request);
        requireArtworkHasDocument(request);
        var response = documentService.findDocumentById(request.getDocumentId());
        log.info("ArtworkFacadeImpl.downloadArtworkDocument - end | request: {}", request);
        return response;
    }

    @Override
    public UploadArtworkDocumentsResponse uploadArtworkDocuments(WithOwner<UploadArtworkDocumentsRequest> withOwner) {
        log.info("ArtworkDocumentFacadeImpl.saveArtworkDocuments - start | withOwner: {}", withOwner);
        var request = withOwner.getRequest();
        request.validateSingletonDocuments();
        var artworkId = artworkService.findArtworkById(withOwner
                .convert(UploadArtworkDocumentsRequest::getArtworkId))
                .getId();
        requireNoDuplicateSingletons(request);
        var category = request.getFileCategory();
        var documents = request
                .getDocuments()
                .stream()
                .map(doc -> documentService.saveDocument(doc, category))
                .collect(toList());
        artworkDocumentService.saveArtworkDocuments(artworkId, documents);
        var response = new UploadArtworkDocumentsResponse(documents);
        log.info("ArtworkDocumentFacadeImpl.saveArtworkDocuments - end | withOwner: {}", withOwner);
        return response;
    }

    @Override
    public void deleteArtworkDocument(WithOwner<ArtworkDocumentRequest> withOwner) {
        log.info("ArtworkFacadeImpl.deleteArtworkDocument - start | withOwner: {}", withOwner);
        var artworkId = artworkService.findArtworkById(withOwner
                .convert(ArtworkDocumentRequest::getArtworkId))
                .getId();
        var request = withOwner.getRequest();
        requireArtworkHasDocument(request);
        var documentId = request.getDocumentId();
        artworkDocumentService.deleteArtworkDocumentById(artworkId, documentId);
        documentService.deleteDocumentById(documentId);
        log.info("ArtworkFacadeImpl.deleteArtworkDocument - end | withOwner: {}", withOwner);
    }

    @Override
    public void setPrincipalImage(WithOwner<SetArtworkPrincipalImageRequest> withOwner) {
        log.info("ArtworkFacadeImpl.setPrincipalImage - start | withOwner: {}", withOwner);
        var artworkId = artworkService.findArtworkById(withOwner
                .convert(SetArtworkPrincipalImageRequest::getArtworkId))
                .getId();
        var request = withOwner.getRequest();
        requireArtworkHasDocumentInValidType(request);
        documentService
                .findDocumentsByArtworkId(artworkId)
                .stream().filter(documentDto -> documentDto.getType().equals(PRINCIPAL_IMAGE))
                .findFirst()
                .ifPresent(documentDto -> documentService.updateDocumentType(documentDto.getId(), IMAGE));
        documentService.updateDocumentType(request.getDocumentId(), PRINCIPAL_IMAGE);
        log.info("ArtworkFacadeImpl.setPrincipalImage - end | withOwner: {}", withOwner);
    }

    private void requireArtworkHasDocument(ArtworkDocumentRequest request) {
        artworkDocumentService
                .findDocumentIdsByArtworkId(request.getArtworkId())
                .stream()
                .filter(request.getDocumentId()::equals)
                .findAny()
                .orElseThrow(ARTWORK_DOCUMENT_NOT_FOUND::newException);
    }

    private void requireNoDuplicateSingletons(UploadArtworkDocumentsRequest request) {
        if (documentService
                .findDocumentsByArtworkId(request.getArtworkId())
                .stream()
                .map(DocumentDto::getType)
                .filter(SINGLETONS::contains)
                .anyMatch(request.getFileCategory()::equals))
            throw ARTWORK_DOCUMENT_ALREADY_ATTACHED.newException();
    }

    private void requireArtworkHasDocumentInValidType(SetArtworkPrincipalImageRequest request) {
        var document = documentService
                .findDocumentsByArtworkId(request.getArtworkId())
                .stream()
                .filter(documentDto -> documentDto.getId().equals(request.getDocumentId()))
                .findFirst();
        if(document.isEmpty()) {
            throw ARTWORK_DOCUMENT_NOT_FOUND.newException();
        }
        if(document.get().getType() != IMAGE && document.get().getType() != PRINCIPAL_IMAGE) {
            throw ARTWORK_DOCUMENT_CANNOT_BE_PRINCIPAL_IMAGE.newException();
        }
    }
}
