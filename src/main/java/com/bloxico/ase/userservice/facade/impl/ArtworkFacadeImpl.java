package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
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
import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.*;

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
    public SubmitArtworkResponse submitArtwork(SubmitArtworkRequest request, long principalId) {
        log.info("ArtworkFacadeImpl.submitArtwork - start | request: {}, principalId: {} ", request, principalId);
        var artworkDto = MAPPER.toArtworkDto(request);
        artworkDto.setGroup(doSaveGroup(request, principalId));
        artworkDto.setOwner(userProfileService.findArtOwnerByUserId(principalId));
        artworkDto.setLocation(doSaveLocation(request, principalId));
        artworkDto.setArtist(doSaveArtist(request, principalId));
        artworkDto.addCategories(doSaveCategories(request, principalId));
        artworkDto.addMaterials(doSaveMaterials(request, principalId));
        artworkDto.addMediums(doSaveMediums(request, principalId));
        artworkDto.addStyles(doSaveStyles(request, principalId));
        artworkDto.addDocuments(doSaveImages(request, principalId));
        artworkDto.addDocuments(Arrays.asList(doSavePrincipalImage(request, principalId)));
        artworkDto.addDocuments(Arrays.asList(doSaveDocument(request, principalId)));
        artworkDto = artworkService.submitArtwork(artworkDto, principalId);
        var response = artworkGroupService.findGroupById(artworkDto.getGroup().getId());
        log.info("ArtworkFacadeImpl.submitArtwork - end | request: {}, principalId: {} ", request, principalId);
        return new SubmitArtworkResponse(response);
    }

    // HELPER METHODS

    private ArtworkGroupDto doSaveGroup(SubmitArtworkRequest request, long principalId) {
        if(request.getGroupId() != null) {
            return artworkGroupService.findGroupById(request.getGroupId());
        }
        return artworkGroupService.saveGroup(principalId);
    }

    private List<ArtworkMetadataDto> doSaveCategories(SubmitArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(categoryService, request.getCategories(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveMaterials(SubmitArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(materialService, request.getMaterials(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveMediums(SubmitArtworkRequest request, long principalId) {
        return doSaveArtworkMetadata(mediumService, request.getMediums(), principalId);
    }

    private List<ArtworkMetadataDto> doSaveStyles(SubmitArtworkRequest request, long principalId) {
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

    private DocumentDto doSavePrincipalImage(SubmitArtworkRequest request, long principalId) {
        return documentService.saveDocument(request.getPrincipalPicture(), FileCategory.PRINCIPAL_IMAGE, principalId);
    }

    private DocumentDto doSaveDocument(SubmitArtworkRequest request, long principalId) {
        if(request.getIAmArtOwner()) {
            if(request.getCv() == null) {
                throw ARTWORK_MISSING_RESUME.newException();
            }
            return documentService.saveDocument(request.getCv(), FileCategory.CV, principalId);
        } else {
            if(request.getCertificate() == null) {
                throw ARTWORK_MISSING_CERTIFICATE.newException();
            }
            return documentService.saveDocument(request.getCertificate(), FileCategory.CERTIFICATE, principalId);
        }

    }

    private List<DocumentDto> doSaveImages(SubmitArtworkRequest request, long principalId) {
        List<DocumentDto> savedImages = new ArrayList<>();
        request.getImages().stream().forEach(image -> {
            var documentDto = documentService.saveDocument(image, FileCategory.IMAGE, principalId);
            savedImages.add(documentDto);
        });
        return savedImages;
    }

    private ArtistDto doSaveArtist(SubmitArtworkRequest request, long principalId) {
        var artistDto = new ArtistDto();
        if(request.getIAmArtOwner()) {
            var artOwnerDto = userProfileService.findArtOwnerByUserId(principalId);
            artistDto.setName(artOwnerDto.getUserProfile().getFirstName() + " " + artOwnerDto.getUserProfile().getLastName());
        } else if(StringUtils.isEmpty(request.getArtist())){
            throw ARTWORK_ARTIST_NOT_PROVIDED.newException();
        } else {
            artistDto.setName(request.getArtist());
        }
        return artistService.saveArtist(artistDto, principalId);
    }

    private LocationDto doSaveLocation(SubmitArtworkRequest request, long principalId) {
        var locationDto = MAPPER.toLocationDto(request);
        locationDto.setCountry(locationService.findCountryByName(request.getCountry()));
        return locationService.saveLocation(locationDto, principalId);
    }
}
