package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContextService;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.artwork.*;
import com.bloxico.ase.userservice.service.artwork.IArtworkDocumentService;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.*;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.DRAFT;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.PENDING;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.*;
import static com.bloxico.ase.userservice.util.Functions.ifNotNull;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_MISSING_CERTIFICATE;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_MISSING_RESUME;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@Transactional
public class ArtworkFacadeImpl implements IArtworkFacade {

    private final ILocationService locationService;
    private final IDocumentService documentService;
    private final IArtworkService artworkService;
    private final IArtistService artistService;
    private final CategoryServiceImpl categoryService;
    private final MaterialServiceImpl materialService;
    private final MediumServiceImpl mediumService;
    private final StyleServiceImpl styleService;
    private final AseSecurityContextService security;
    private final IArtworkDocumentService artworkDocumentService;

    @Autowired
    public ArtworkFacadeImpl(ILocationService locationService,
                             IDocumentService documentService,
                             IArtworkService artworkService,
                             IArtistService artistService,
                             CategoryServiceImpl categoryService,
                             MaterialServiceImpl materialService,
                             MediumServiceImpl mediumService,
                             StyleServiceImpl styleService,
                             AseSecurityContextService security,
                             IArtworkDocumentService artworkDocumentService)
    {
        this.locationService = locationService;
        this.documentService = documentService;
        this.artworkService = artworkService;
        this.artistService = artistService;
        this.categoryService = categoryService;
        this.materialService = materialService;
        this.mediumService = mediumService;
        this.styleService = styleService;
        this.security = security;
        this.artworkDocumentService = artworkDocumentService;
    }

    @Override
    public ArtworkResponse createArtworkDraft() {
        log.info("ArtworkFacadeImpl.createArtworkDraft - start");
        var artwork = new ArtworkDto();
        artwork.setStatus(DRAFT);
        artwork.setOwnerId(security.getArtOwnerId());
        artwork = artworkService.saveArtwork(artwork);
        var response = new ArtworkResponse(artwork);
        log.info("ArtworkFacadeImpl.createArtworkDraft - end");
        return response;
    }

    @Override
    public DetailedArtworkResponse findArtworkById(WithOwner<FindByArtworkIdRequest> withOwner) {
        log.info("ArtworkFacadeImpl.findArtworkById - start | withOwner: {}", withOwner);
        var artwork = artworkService.findArtworkById(withOwner
                .update(FindByArtworkIdRequest::getId));
        var response = toDetailedArtworkResponse(artwork);
        log.info("ArtworkFacadeImpl.findArtworkById - end | withOwner: {}", withOwner);
        return response;
    }

    @Override
    public DetailedArtworkResponse updateArtworkData(WithOwner<UpdateArtworkDataRequest> withOwner) {
        log.info("ArtworkFacadeImpl.updateArtworkData - start | withOwner: {}", withOwner);
        var artwork = doUpdateArtwork(withOwner);
        var response = toDetailedArtworkResponse(artwork);
        log.info("ArtworkFacadeImpl.updateArtworkData - end | withOwner: {}", withOwner);
        return response;
    }

    @Override
    public SearchArtworkResponse searchArtworks(WithOwner<SearchArtworkRequest> withOwner, PageRequest page) {
        log.info("ArtworkFacadeImpl.searchArtworks - start | withOwner: {}, page: {}", withOwner, page);
        var result = artworkService.searchArtworks(withOwner, page);
        var response = new SearchArtworkResponse(result);
        log.info("ArtworkFacadeImpl.searchArtworks - end | withOwner: {}, page: {}", withOwner, page);
        return response;
    }

    @Override
    public void deleteArtwork(WithOwner<DeleteArtworkRequest> withOwner) {
        log.info("ArtworkFacadeImpl.deleteArtwork - start | withOwner: {}", withOwner);
        var artworkId = artworkService.findArtworkById(withOwner
                .update(DeleteArtworkRequest::getArtworkId))
                .getId();
        var documentIDs = artworkDocumentService.findDocumentIdsByArtworkId(artworkId);
        artworkDocumentService.deleteArtworkDocumentsByArtworkId(artworkId);
        documentService.deleteDocumentsByIds(documentIDs);
        artworkService.deleteArtworkById(artworkId);
        log.info("ArtworkFacadeImpl.deleteArtwork - end | withOwner: {}", withOwner);
    }

    // HELPER METHODS

    private ArtworkDto doUpdateArtwork(WithOwner<UpdateArtworkDataRequest> withOwner) {
        var artwork = artworkService.findArtworkById(withOwner
                .update(UpdateArtworkDataRequest::getArtworkId));
        var request = withOwner.getRequest();
        if (DRAFT != request.getStatus())
            validateRequiredDocuments(request);
        MAPPER.update(request, artwork);
        doSetPrincipalImage(request.getPrincipalImageId());
        artwork.setLocationId(doSaveLocation(request).getId());
        artwork.setArtist(doSaveArtist(request));
        artwork.addCategories(doSaveArtworkMetadata(categoryService, request.getCategories()));
        artwork.addMaterials(doSaveArtworkMetadata(materialService, request.getMaterials()));
        artwork.addMediums(doSaveArtworkMetadata(mediumService, request.getMediums()));
        artwork.addStyles(doSaveArtworkMetadata(styleService, request.getStyles()));
        return artworkService.saveArtwork(artwork);
    }

    private void validateRequiredDocuments(UpdateArtworkDataRequest request) {
        var artworkId = request.getArtworkId();
        var documents = artworkDocumentService.findDocumentIdsByArtworkId(artworkId);
        var documentTypes = documentService
                .findDocumentsByIds(documents)
                .stream()
                .map(DocumentDto::getType)
                .collect(toSet());
        var iAmArtOwner = request.getIAmArtOwner();
        if (iAmArtOwner && !documentTypes.contains(CV))
            throw ARTWORK_MISSING_RESUME.newException();
        if (!iAmArtOwner && !documentTypes.contains(CERTIFICATE))
            throw ARTWORK_MISSING_CERTIFICATE.newException();
    }

    private void doSetPrincipalImage(Long documentId) {
        documentService.updateDocumentType(documentId, PRINCIPAL_IMAGE);
    }

    private LocationDto doSaveLocation(UpdateArtworkDataRequest request) {
        var locationDto = MAPPER.toLocationDto(request);
        var countryDto = locationService.findCountryByName(request.getCountry());
        locationDto.setCountry(countryDto);
        return locationService.saveLocation(locationDto);
    }

    private ArtistDto doSaveArtist(UpdateArtworkDataRequest request) {
        var artistDto = new ArtistDto();
        artistDto.setName(request.getArtist());
        return artistService.saveArtist(artistDto);
    }

    private List<ArtworkMetadataDto> doSaveArtworkMetadata(IArtworkMetadataService service,
                                                           List<String> artworkMetadataDtos)
    {
        return artworkMetadataDtos
                .stream()
                .map(name -> new ArtworkMetadataDto(name, PENDING))
                .map(service::findOrSaveArtworkMetadata)
                .collect(toList());
    }

    private DetailedArtworkResponse toDetailedArtworkResponse(ArtworkDto artwork) {
        var location = ifNotNull(artwork.getLocationId(), locationService::findLocationById);
        var documents = documentService.findDocumentsByArtworkId(artwork.getId());
        return new DetailedArtworkResponse(artwork, location, documents);
    }

}
