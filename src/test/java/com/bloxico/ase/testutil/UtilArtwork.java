package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.*;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.entity.artwork.Artist;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.facade.impl.ArtworkFacadeImpl;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.service.artwork.document.impl.ArtworkDocumentServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkServiceImpl;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @Autowired private UserProfileServiceImpl userProfileService;
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

    public ArtworkHistoryDto genArtworkHistory() {
        var artworkHistoryDto = new ArtworkHistoryDto();
        artworkHistoryDto.setAppraisalHistory(genUUID());
        artworkHistoryDto.setLocationHistory(genUUID());
        artworkHistoryDto.setRunsHistory(genUUID());
        artworkHistoryDto.setMaintenanceHistory(genUUID());
        artworkHistoryDto.setNotes(genUUID());
        return artworkHistoryDto;
    }

    public ArtworkDto genArtworkDto() {
        var artworkDto = new ArtworkDto();
        artworkDto.setTitle(genUUID());
        artworkDto.setYear(genPosInt(Year.now().getValue()));
        artworkDto.setWeight(genPosBigDecimal(20));
        artworkDto.setHeight(genPosBigDecimal(20));
        artworkDto.setWidth(genPosBigDecimal(20));
        artworkDto.setDepth(genPosBigDecimal(20));
        artworkDto.setPhone(genUUID());
        artworkDto.setStatus(Artwork.Status.WAITING_FOR_EVALUATION);
        artworkDto.setOwnerId(utilUserProfile.savedArtOwnerDto().getId());
        artworkDto.setLocation(utilLocation.savedLocationDto());
        artworkDto.setArtist(savedArtistDto());
        artworkDto.addCategories(List.of(utilArtworkMetadata.savedArtworkMetadataDto(CATEGORY, PENDING)));
        artworkDto.addMaterials(List.of(utilArtworkMetadata.savedArtworkMetadataDto(MATERIAL, PENDING)));
        artworkDto.addMediums(List.of(utilArtworkMetadata.savedArtworkMetadataDto(MEDIUM, PENDING)));
        artworkDto.addStyles(List.of(utilArtworkMetadata.savedArtworkMetadataDto(STYLE, PENDING)));
        artworkDto.setArtworkHistory(genArtworkHistory());
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(IMAGE)));
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(PRINCIPAL_IMAGE)));
        artworkDto.addDocuments(List.of(utilDocument.savedDocumentDto(CERTIFICATE)));
        return artworkDto;
    }

    public ArtworkDto savedArtworkDto() {
        var artworkDto = genArtworkDto();
        return artworkService.saveArtwork(artworkDto);
    }

    public ArtworkDto savedArtworkDtoDraft() {
        return artworkFacade.saveArtworkDraft().getArtworkDto();
    }

    public ArtworkDto savedArtworkDtoWithOwner() {
        var artOwner = utilUserProfile.savedArtOwnerDto(utilUser.savedUser().getId());
        var artworkDto = new ArtworkDto();
        artworkDto.setStatus(Artwork.Status.DRAFT);
        artworkDto.setOwnerId(artOwner.getId());
        return artworkService.saveArtwork(artworkDto);
    }

    public ArtworkDto savedArtworkDtoDraftWithDocuments() {
        var artworkDto = savedArtworkDtoDraft();
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
        var artworkDto = savedArtworkDtoDraftWithDocuments();
        var imageId = artworkDto.getDocuments().stream().filter(documentDto -> IMAGE.equals(documentDto.getType())).findFirst().get().getId();
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
        map.put("artworkId", String.valueOf(savedArtworkDtoWithOwner().getId()));
        map.put("fileCategory", fileCategory.name());
        return map;
    }

    public SaveArtworkRequest genSaveArtworkRequest(Artwork.Status status, boolean artOwner) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithRegion(region);
        var fileCategory = artOwner ? CV : CERTIFICATE;
        return new SaveArtworkRequest(
                List.of(genMultipartFile(IMAGE)),               // images
                genMultipartFile(PRINCIPAL_IMAGE),              // principalImage
                genUUID(),                                      // title
                new String[]{genUUID(), genUUID()},             // categories
                genUUID(),                                      // artist
                artOwner,                                       // iAmArtOwner
                genPosInt(Year.now().getValue()),               // year
                new String[]{genUUID(), genUUID()},             // materials
                new String[]{genUUID(), genUUID()},             // mediums
                new String[]{genUUID(), genUUID()},             // styles
                genMultipartFile(fileCategory),                 // document
                fileCategory,                                   // fileCategory
                genPosBigDecimal(100),                          // weight
                genPosBigDecimal(100),                          // height
                genPosBigDecimal(100),                          // width
                genPosBigDecimal(100),                          // depth
                genUUID(),                                      // address
                genUUID(),                                      // address2
                genUUID(),                                      // city
                country.getName(),                              // country
                region.getName(),                               // region
                genUUID(),                                      // zipCode
                genUUID(),                                      // phone
                genUUID(),                                      // appraisalHistory
                genUUID(),                                      // locationHistory
                genUUID(),                                      // runsHistory
                genUUID(),                                      // maintenanceHistory
                genUUID(),                                      // notes
                status                                          // status
        );
    }

    public Map<String, String> genSaveArtworkFormParams(Artwork.Status status, boolean iAmArtOwner) {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithRegion(region);
        var map = new HashMap<String, String>();
        map.put("title", genUUID());
        map.put("categories", genUUID());
        map.put("artist", genUUID());
        map.put("iAmArtOwner", Boolean.toString(iAmArtOwner));
        map.put("year", "2010");
        map.put("materials", genUUID());
        map.put("mediums", genUUID());
        map.put("styles", genUUID());
        map.put("fileCategory", iAmArtOwner ? CV.name() : CERTIFICATE.name());
        map.put("weight", genPosBigDecimal(100).toString());
        map.put("height", genPosBigDecimal(100).toString());
        map.put("width", genPosBigDecimal(100).toString());
        map.put("depth", genPosBigDecimal(100).toString());
        map.put("address", genUUID());
        map.put("address2", genUUID());
        map.put("city", genUUID());
        map.put("country", country.getName());
        map.put("region", region.getName());
        map.put("zipCode", genUUID());
        map.put("phone", genUUID());
        map.put("appraisalHistory", genUUID());
        map.put("locationHistory", genUUID());
        map.put("runsHistory", genUUID());
        map.put("maintenanceHistory", genUUID());
        map.put("notes", genUUID());
        map.put("status", status.name());
        return map;
    }

}
