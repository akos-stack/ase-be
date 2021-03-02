package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkHistoryDto;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.facade.IArtworkFacade;
import com.bloxico.ase.userservice.service.address.ILocationService;
import com.bloxico.ase.userservice.service.artwork.IArtistService;
import com.bloxico.ase.userservice.service.artwork.IArtworkGroupService;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.CategoryServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.MaterialServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.MediumServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.StyleServiceImpl;
import com.bloxico.ase.userservice.service.document.IDocumentService;
import com.bloxico.ase.userservice.service.user.IUserProfileService;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
@Transactional
public class ArtworkFacadeImpl implements IArtworkFacade {

    private final ILocationService locationService;
    private final IDocumentService documentService;
    private final IArtworkService artworkService;
    private final IArtistService artistService;
    private final IUserProfileService userProfileService;
    private final IArtworkGroupService artworkGroupService;
    private final CategoryServiceImpl categoryService;
    private final MaterialServiceImpl materialService;
    private final MediumServiceImpl mediumService;
    private final StyleServiceImpl styleService;

    public ArtworkFacadeImpl(ILocationService locationService, IDocumentService documentService, IArtworkService artworkService, IArtistService artistService, IUserProfileService userProfileService, CategoryServiceImpl categoryService, MaterialServiceImpl materialService, MediumServiceImpl mediumService, StyleServiceImpl styleService, IArtworkGroupService artworkGroupService) {
        this.locationService = locationService;
        this.documentService = documentService;
        this.artworkService = artworkService;
        this.artistService = artistService;
        this.userProfileService = userProfileService;
        this.categoryService = categoryService;
        this.materialService = materialService;
        this.mediumService = mediumService;
        this.styleService = styleService;
        this.artworkGroupService = artworkGroupService;
    }

    @Override
    public SaveArtworkResponse submitArtwork(SaveArtworkRequest request, long principalId) {
        log.info("ArtworkFacadeImpl.submitArtwork - start | request: {}, principalId: {} ", request, principalId);
        var artworkDto = doPrepareArtworkDto(request, principalId);
        artworkDto = artworkService.saveArtwork(artworkDto, principalId);
        log.info("ArtworkFacadeImpl.submitArtwork - end | request: {}, principalId: {} ", request, principalId);
        return new SaveArtworkResponse(artworkDto.getGroup());
    }

    // HELPER METHODS

    private ArtworkDto doPrepareArtworkDto(SaveArtworkRequest request, long principalId) {
        request.validateRequest();
        var artworkDto = MAPPER.toArtworkDto(request);
        artworkDto.setGroup(doSaveGroup(request, principalId));
        artworkDto.setOwner(userProfileService.findArtOwnerByUserId(principalId));
        artworkDto.setLocation(doSaveLocation(request, principalId));
        artworkDto.setArtist(doSaveArtist(request, principalId));
        artworkDto.addCategories(doSaveCategories(request, principalId));
        artworkDto.addMaterials(doSaveMaterials(request, principalId));
        artworkDto.addMediums(doSaveMediums(request, principalId));
        artworkDto.addStyles(doSaveStyles(request, principalId));
        artworkDto.setHistory(doPrepareArtworkHistory(request));
        artworkDto.addDocuments(Collections.singletonList(doSaveDocument(request, principalId)));
        artworkDto.addDocuments(doSaveImages(request, principalId));
        artworkDto.addDocuments(Collections.singletonList(doSavePrincipalImage(request, principalId)));
        return artworkDto;
    }

    private ArtworkHistoryDto doPrepareArtworkHistory(SaveArtworkRequest request) {
        if(request.hasHistory()) {
            return MAPPER.toArtworkHistoryDto(request);
        } else return null;
    }

    private ArtworkGroupDto doSaveGroup(SaveArtworkRequest request, long principalId) {
        var artworkGroupDto = MAPPER.toArtworkGroupDto(request);
        if(request.getGroupId() != null) {
            return artworkGroupService.findOrUpdateGroup(artworkGroupDto, principalId);
        }
        return artworkGroupService.saveGroup(artworkGroupDto, principalId);
    }

    private List<ArtworkMetadataDto> doSaveCategories(SaveArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(categoryService, request.getCategories(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveMaterials(SaveArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(materialService, request.getMaterials(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveMediums(SaveArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(mediumService, request.getMediums(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveStyles(SaveArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(styleService, request.getStyles(), principalId);
    }
    private List<ArtworkMetadataDto> doSaveArtworkMetadata(IArtworkMetadataService service, String [] artworkMetadataDtos, long principalId) {
        List<ArtworkMetadataDto> result = new ArrayList<>();
        for(String s: artworkMetadataDtos) {
            var dto = new ArtworkMetadataDto();
            dto.setName(s);
            dto.setStatus(ArtworkMetadata.Status.PENDING);
            result.add(service.findOrSaveArtworkMetadata(dto, principalId));
        }
        return result;
    }

    private DocumentDto doSavePrincipalImage(SaveArtworkRequest request, long principalId) {
        return documentService.saveDocument(request.getPrincipalImage(), FileCategory.PRINCIPAL_IMAGE, principalId);
    }

    private DocumentDto doSaveDocument(SaveArtworkRequest request, long principalId) {
       return documentService.saveDocument(request.getDocument(), request.getFileCategory(), principalId);
    }

    private List<DocumentDto> doSaveImages(SaveArtworkRequest request, long principalId) {
        List<DocumentDto> savedImages = new ArrayList<>();
        request.getImages().forEach(image -> {
            var documentDto = documentService.saveDocument(image, FileCategory.IMAGE, principalId);
            savedImages.add(documentDto);
        });
        return savedImages;
    }

    private ArtistDto doSaveArtist(SaveArtworkRequest request, long principalId) {
        var artistDto = new ArtistDto();
        artistDto.setName(request.getArtist());
        return artistService.saveArtist(artistDto, principalId);
    }

    private LocationDto doSaveLocation(SaveArtworkRequest request, long principalId) {
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCountry(locationService.findCountryByName(request.getCountry()));
        return locationService.saveLocation(locationDto, principalId);
    }
}