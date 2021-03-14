package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.Artist;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.facade.impl.ArtworkFacadeImpl;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.service.artwork.document.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Year;
import java.util.*;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.PENDING;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.*;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.*;

@Component
public class UtilArtwork {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private UtilSecurityContext utilSecurityContext;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private ArtworkDocumentServiceImpl artworkDocumentService;

    public Artist savedArtist() {
        var creatorId = utilUser.savedAdmin().getId();
        var artist = new Artist();
        artist.setName(genUUID());
        artist.setCreatorId(creatorId);
        return artistRepository.saveAndFlush(artist);
    }

    public ArtistDto savedArtistDto() {
        return MAPPER.toDto(savedArtist());
    }

    public ArtworkDto genArtworkDto(Artwork.Status status) {
        Long ownerId;
        if (!utilSecurityContext.isArtOwner()) {
            var artOwner = utilUserProfile.savedArtOwnerDto(utilUser.savedUser().getId());
            ownerId = artOwner.getId();
        } else {
            ownerId = utilSecurityContext.getLoggedInArtOwner().getId();
        }
        var artworkDto = new ArtworkDto();
        artworkDto.setTitle(genUUID());
        artworkDto.setYear(genPosInt(Year.now().getValue()));
        artworkDto.setWeight(genPosBigDecimal(20));
        artworkDto.setHeight(genPosBigDecimal(20));
        artworkDto.setWidth(genPosBigDecimal(20));
        artworkDto.setDepth(genPosBigDecimal(20));
        artworkDto.setPhone(genUUID());
        artworkDto.setStatus(status);
        artworkDto.setOwnerId(ownerId);
        artworkDto.setLocation(utilLocation.savedLocationDto());
        artworkDto.setArtist(savedArtistDto());
        artworkDto.addCategories(List.of(utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, PENDING)));
        artworkDto.addMaterials(List.of(utilArtworkMetadata.savedArtworkMetadataDto(MATERIAL, PENDING)));
        artworkDto.addMediums(List.of(utilArtworkMetadata.savedArtworkMetadataDto(MEDIUM, PENDING)));
        artworkDto.addStyles(List.of(utilArtworkMetadata.savedArtworkMetadataDto(STYLE, PENDING)));
        artworkDto.setAppraisalHistory(genUUID());
        artworkDto.setLocationHistory(genUUID());
        artworkDto.setRunsHistory(genUUID());
        artworkDto.setMaintenanceHistory(genUUID());
        artworkDto.setNotes(genUUID());
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(IMAGE)));
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(PRINCIPAL_IMAGE)));
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(CERTIFICATE)));
        return artworkDto;
    }

    public ArtworkDto savedArtworkDto(Artwork.Status... status) {
        var artworkDto = genArtworkDto(status.length > 0 ? status[0] : Artwork.Status.DRAFT);
        return savedArtworkDocuments(artworkService.saveArtwork(artworkDto));
    }

    public ArtworkDto savedArtworkDtoDraft() {
        return artworkFacade.saveArtworkDraft().getArtworkDto();
    }

    public ArtworkDto savedArtworkDtoDraftWithOwner(Artwork.Status... status) {
        var artOwner = utilUserProfile.savedArtOwnerDto(utilUser.savedUser().getId());
        var artworkDto = new ArtworkDto();
        artworkDto.setStatus(status.length > 0 ? status[0] : Artwork.Status.DRAFT);
        artworkDto.setOwnerId(artOwner.getId());
        return artworkService.saveArtwork(artworkDto);
    }

    public ArtworkDto savedArtworkDtoDraftWithDocuments() {
        return savedArtworkDocuments(savedArtworkDtoDraft());
    }

    public ArtworkDto savedArtworkDocuments(ArtworkDto artworkDto) {
        List<DocumentDto> documentIds = new ArrayList<>();
        for (var category : FileCategory.values()) {
            var documentDto = utilDocument.savedDocumentDto(category);
            documentIds.add(documentDto);
        }
        artworkDocumentService.saveArtworkDocuments(artworkDto.getId(), documentIds);
        return artworkFacade.getArtworkById(artworkDto.getId()).getArtworkDto();
    }

    public SaveArtworkDocumentsRequest genSaveArtworkDocumentsRequest() {
        var request = new SaveArtworkDocumentsRequest();
        request.setArtworkId(savedArtworkDtoDraft().getId());
        request.setDocuments(List.of(genMultipartFile(IMAGE)));
        request.setFileCategory(IMAGE);
        return request;
    }

    public SaveArtworkDataRequest genSaveArtworkDataRequest(Artwork.Status status, boolean artOwner) {
        return genSaveArtworkDataRequestWithDocuments(savedArtworkDtoDraft(),
                status, artOwner);
    }

    public SaveArtworkDataRequest genSaveArtworkDataRequestWithOwner(Artwork.Status status, boolean artOwner) {
        return genSaveArtworkDataRequestWithDocuments(savedArtworkDtoDraftWithOwner(),
                status, artOwner);
    }

    public SaveArtworkDataRequest genSaveArtworkDataRequestWithDocuments(Artwork.Status status, boolean artOwner) {
        return genSaveArtworkDataRequestWithDocuments(savedArtworkDtoDraftWithDocuments(),
                status, artOwner);
    }

    public SaveArtworkDataRequest genSaveArtworkDataRequestWithDocuments(ArtworkDto artworkDto, Artwork.Status status, boolean artOwner) {
        var imageId = -1L;
        if (!CollectionUtils.isEmpty(artworkDto.getDocuments())) {
            imageId = artworkDto.getDocuments().stream().filter(documentDto -> IMAGE.equals(documentDto.getType())).findFirst().get().getId();
        }
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithRegion(region);
        return new SaveArtworkDataRequest(
                artworkDto.getId(),                   // artworkId
                imageId,                              // principal_image_id
                genUUID(),                            // title
                Arrays.asList(genUUID(), genUUID()),  // categories
                genUUID(),                            // artist
                artOwner,                             // iAmArtOwner
                genPosInt(Year.now().getValue()),     // year
                Arrays.asList(genUUID(), genUUID()),  // materials
                Arrays.asList(genUUID(), genUUID()),  // mediums
                Arrays.asList(genUUID(), genUUID()),  // styles
                genPosBigDecimal(100),                          // weight
                genPosBigDecimal(100),                          // height
                genPosBigDecimal(100),                          // width
                genPosBigDecimal(100),                          // depth
                genUUID(),                             // address
                genUUID(),                             // address2
                genUUID(),                             // city
                country.getName(),                     // country
                region.getName(),                      // region
                genUUID(),                             // zipCode
                genUUID(),                             // phone
                genUUID(),                             // appraisalHistory
                genUUID(),                             // locationHistory
                genUUID(),                             // runsHistory
                genUUID(),                             // maintenanceHistory
                genUUID(),                             // notes
                status                                 // status
        );
    }

    public Map<String, String> genSaveArtworkDocumentsFormParams(FileCategory fileCategory) {
        var map = new HashMap<String, String>();
        map.put("artworkId", String.valueOf(savedArtworkDtoDraft().getId()));
        map.put("fileCategory", fileCategory.name());
        return map;
    }

    public Map<String, String> genSaveArtworkDocumentsFormParamsWithOwner(FileCategory fileCategory) {
        var map = new HashMap<String, String>();
        map.put("artworkId", String.valueOf(savedArtworkDtoDraftWithOwner().getId()));
        map.put("fileCategory", fileCategory.name());
        return map;
    }

    public static SearchArtworkRequest genSearchArtworksRequest(Artwork.Status status) {
        return new SearchArtworkRequest(status, null);
    }

}
