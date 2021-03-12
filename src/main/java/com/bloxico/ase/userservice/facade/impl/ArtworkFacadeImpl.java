package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContextService;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkHistoryDto;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.artwork.IArtistService;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.service.artwork.document.IArtworkDocumentService;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.CategoryServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.MaterialServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.MediumServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.StyleServiceImpl;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bloxico.ase.userservice.entity.artwork.Artwork.Status.DRAFT;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_MISSING_CERTIFICATE;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_MISSING_RESUME;

@Slf4j
@Service
@Transactional
public class ArtworkFacadeImpl implements IArtworkFacade {

    private final ILocationService locationService;
    private final IDocumentService documentService;
    private final IArtworkService artworkService;
    private final IArtistService artistService;
    private final IUserProfileService userProfileService;
    private final CategoryServiceImpl categoryService;
    private final MaterialServiceImpl materialService;
    private final MediumServiceImpl mediumService;
    private final StyleServiceImpl styleService;
    private final AseSecurityContextService securityContextService;
    private final IArtworkDocumentService artworkDocumentService;

    @Autowired
    public ArtworkFacadeImpl(ILocationService locationService, IDocumentService documentService, IArtworkService artworkService, IArtistService artistService, IUserProfileService userProfileService, CategoryServiceImpl categoryService, MaterialServiceImpl materialService, MediumServiceImpl mediumService, StyleServiceImpl styleService, AseSecurityContextService securityContextService, IArtworkDocumentService artworkDocumentService) {
        this.locationService = locationService;
        this.documentService = documentService;
        this.artworkService = artworkService;
        this.artistService = artistService;
        this.userProfileService = userProfileService;
        this.categoryService = categoryService;
        this.materialService = materialService;
        this.mediumService = mediumService;
        this.styleService = styleService;
        this.securityContextService = securityContextService;
        this.artworkDocumentService = artworkDocumentService;
    }

    @Override
    public SaveArtworkResponse getArtworkById(Long id) {
        log.info("ArtworkFacadeImpl.saveArtworkDraft - start | id: {}", id);
        var artworkDto = artworkService.getArtworkById(id);
        securityContextService.validateOwner(artworkDto.getOwnerId());
        log.info("ArtworkFacadeImpl.saveArtworkDraft - end | id: {}", id);
        return new SaveArtworkResponse(prepareResponse(artworkDto));
    }

    @Override
    public SaveArtworkResponse saveArtworkDraft() {
        log.info("ArtworkFacadeImpl.saveArtworkDraft - start");
        var artworkDto = new ArtworkDto();
        artworkDto.setStatus(DRAFT);
        artworkDto.setOwnerId(userProfileService.findArtOwnerByUserId(securityContextService.getPrincipalId()).getId());
        artworkDto = artworkService.saveArtwork(artworkDto);
        log.info("ArtworkFacadeImpl.saveArtworkDraft - end");
        return new SaveArtworkResponse(prepareResponse(artworkDto));
    }

    @Override
    public SaveArtworkResponse saveArtworkData(SaveArtworkDataRequest request) {
        log.info("ArtworkFacadeImpl.saveArtworkData - start | request: {}", request);
        var artworkDto = artworkService.getArtworkById(request.getArtworkId());
        securityContextService.validateOwner(artworkDto.getOwnerId());
        artworkDto = doPrepareArtworkDto(request, artworkDto);
        artworkDto = artworkService.saveArtwork(artworkDto);
        log.info("ArtworkFacadeImpl.saveArtworkData - end | request: {}", request);
        return new SaveArtworkResponse(prepareResponse(artworkDto));
    }

    @Override
    public SearchArtworkResponse searchArtworks(SearchArtworkRequest request, PageRequest page) {
        log.info("ArtworkFacadeImpl.searchArtworks - start | request: {}, page: {}", request, page);
        var result = artworkService.searchArtworks(request, page);
        var response = new SearchArtworkResponse(result);
        log.info("ArtworkFacadeImpl.searchArtworks - end | request: {}, page: {}", request, page);
        return response;
    }

    @Override
    public void deleteArtwork(Long id) {
        log.info("ArtworkFacadeImpl.deleteArtwork - start | id: {}", id);
        var artworkDto = artworkService.getArtworkById(id);
        securityContextService.validateOwner(artworkDto.getOwnerId());
        var documentIDs = artworkDocumentService.findDocumentsByArtworkId(id);
        documentIDs.forEach(docId -> artworkDocumentService.removeArtworkDocument(new ArtworkDocument.Id(id, docId)));
        documentService.deleteDocumentsByIds(documentIDs);
        artworkService.deleteArtworkById(id);
        log.info("ArtworkFacadeImpl.deleteArtwork - end | id: {}", id);
    }

    private ArtworkDto prepareResponse(ArtworkDto artworkDto) {
        LocationDto locationDto = null;
        if(artworkDto.getLocation().getId() != null) {
            locationDto = locationService.findLocationById(artworkDto.getLocation().getId());
        }
        List<DocumentDto> documentDtos = documentService.getDocumentsById(artworkDocumentService.findDocumentsByArtworkId(artworkDto.getId()));
        return MAPPER.toArtworkDto(artworkDto, locationDto, Set.copyOf(documentDtos));
    }

    private void validateRequiredDocuments(Long artworkId, boolean iAmArtOwner) {
        var documents =  artworkDocumentService.findDocumentsByArtworkId(artworkId);
        var documentDtos = documentService.getDocumentsById(documents);
        if(iAmArtOwner && documentDtos.stream().noneMatch(documentDto -> FileCategory.CV == documentDto.getType())) {
            throw ARTWORK_MISSING_RESUME.newException();
        }

        if(iAmArtOwner && documentDtos.stream().noneMatch(documentDto -> FileCategory.CERTIFICATE == documentDto.getType())) {
            throw ARTWORK_MISSING_CERTIFICATE.newException();
        }
    }

    private ArtworkDto doPrepareArtworkDto(SaveArtworkDataRequest request, ArtworkDto artworkDtoDb) {
        if(DRAFT != request.getStatus()) {
            validateRequiredDocuments(request.getArtworkId(), request.getIAmArtOwner());
        }
        var artworkDto = MAPPER.toArtworkDto(request);
        doSetPrincipalImage(request.getPrincipalImageId());
        artworkDto.setOwnerId(artworkDtoDb.getOwnerId());
        artworkDto.setLocation(doSaveLocation(request));
        artworkDto.setArtist(doSaveArtist(request));
        artworkDto.addCategories(doSaveCategories(request));
        artworkDto.addMaterials(doSaveMaterials(request));
        artworkDto.addMediums(doSaveMediums(request));
        artworkDto.addStyles(doSaveStyles(request));
        artworkDto.setArtworkHistory(doPrepareArtworkHistory(artworkDtoDb.getArtworkHistory(), request));

        artworkDto.setCreatorId(artworkDtoDb.getCreatorId());
        artworkDto.setCreatedAt(artworkDtoDb.getCreatedAt());
        artworkDto.setUpdatedAt(artworkDtoDb.getUpdatedAt());
        artworkDto.setUpdaterId(artworkDtoDb.getUpdaterId());
        artworkDto.setVersion(artworkDtoDb.getVersion());
        return artworkDto;
    }

    private ArtworkHistoryDto doPrepareArtworkHistory(ArtworkHistoryDto artworkHistoryDtoDb, SaveArtworkDataRequest request) {
        if(request.hasHistory()) {
            if(artworkHistoryDtoDb != null) {
                artworkHistoryDtoDb.setRunsHistory(request.getRunsHistory());
                artworkHistoryDtoDb.setMaintenanceHistory(request.getMaintenanceHistory());
                artworkHistoryDtoDb.setAppraisalHistory(request.getAppraisalHistory());
                artworkHistoryDtoDb.setLocationHistory(request.getLocationHistory());
                artworkHistoryDtoDb.setNotes(request.getNotes());
                return artworkHistoryDtoDb;
            } else {
                return MAPPER.toArtworkHistoryDto(request);
            }
        } else return null;
    }

    private List<ArtworkMetadataDto> doSaveCategories(SaveArtworkDataRequest request) {
        return doSaveArtworkMetadata(categoryService, request.getCategories());
    }

    private List<ArtworkMetadataDto> doSaveMaterials(SaveArtworkDataRequest request) {
        return doSaveArtworkMetadata(materialService, request.getMaterials());
    }

    private List<ArtworkMetadataDto> doSaveMediums(SaveArtworkDataRequest request) {
        return doSaveArtworkMetadata(mediumService, request.getMediums());
    }

    private List<ArtworkMetadataDto> doSaveStyles(SaveArtworkDataRequest request) {
        return doSaveArtworkMetadata(styleService, request.getStyles());
    }
    private List<ArtworkMetadataDto> doSaveArtworkMetadata(IArtworkMetadataService service, List<String> artworkMetadataDtos) {
        List<ArtworkMetadataDto> result = new ArrayList<>();
        for(String s: artworkMetadataDtos) {
            var dto = new ArtworkMetadataDto();
            dto.setName(s);
            dto.setStatus(ArtworkMetadata.Status.PENDING);
            result.add(service.findOrSaveArtworkMetadata(dto));
        }
        return result;
    }

    private void doSetPrincipalImage(Long documentId) {
        documentService.updateDocumentType(documentId, FileCategory.PRINCIPAL_IMAGE);
    }

    private ArtistDto doSaveArtist(SaveArtworkDataRequest request) {
        var artistDto = new ArtistDto();
        artistDto.setName(request.getArtist());
        return artistService.saveArtist(artistDto);
    }

    private LocationDto doSaveLocation(SaveArtworkDataRequest request) {
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCountry(locationService.findCountryByName(request.getCountry()));
        return locationService.saveLocation(locationDto, null);
    }
}
