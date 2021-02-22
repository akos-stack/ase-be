package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkHistoryDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.entity.artwork.Artist;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkGroupRepository;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.bloxico.ase.testutil.Util.genMultipartFile;
import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilArtwork {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private UserProfileServiceImpl userProfileService;
    @Autowired private ArtworkGroupRepository artworkGroupRepository;
    @Autowired private ArtistRepository artistRepository;

    public ArtworkGroupDto savedArtworkGroupDto(ArtworkGroup.Status status) {
        return MAPPER.toDto(savedArtworkGroup(status));
    }

    public ArtworkGroup savedArtworkGroup(ArtworkGroup.Status status) {
        var creatorId = utilUser.savedAdmin().getId();
        var artworkGroup = new ArtworkGroup();
        artworkGroup.setStatus(status);
        artworkGroup.setCreatorId(creatorId);
        return artworkGroupRepository.saveAndFlush(artworkGroup);
    }

    public ArtistDto savedArtistDto() {
        return MAPPER.toDto(savedArtist());
    }

    public Artist savedArtist() {
        var creatorId = utilUser.savedAdmin().getId();
        var artist = new Artist();
        artist.setName(genUUID());
        artist.setCreatorId(creatorId);
        return artistRepository.saveAndFlush(artist);
    }

    public ArtOwnerDto savedArtOwnerDto() {
        var userProfileDto = utilUserProfile.savedUserProfileDto();
        var principalId = userProfileDto.getUserId();
        var artOwnerDto = new ArtOwnerDto();
        artOwnerDto.setUserProfile(userProfileDto);
        return userProfileService.saveArtOwner(artOwnerDto, principalId);
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
        artworkDto.setYear(2010);
        artworkDto.setWeight(new BigDecimal(12));
        artworkDto.setHeight(new BigDecimal(12));
        artworkDto.setWidth(new BigDecimal(12));
        artworkDto.setDepth(new BigDecimal(12));
        artworkDto.setPhone(genUUID());
        artworkDto.setGroup(savedArtworkGroupDto(ArtworkGroup.Status.WAITING_FOR_EVALUATION));
        artworkDto.setOwner(savedArtOwnerDto());
        artworkDto.setLocation(utilLocation.savedLocationDto());
        artworkDto.setArtist(savedArtistDto());
        artworkDto.addCategories(Collections.singletonList(utilArtworkMetadata.savedArtworkMetadataDto(ArtworkMetadata.Type.CATEGORY, ArtworkMetadata.Status.PENDING)));
        artworkDto.addMaterials(Collections.singletonList(utilArtworkMetadata.savedArtworkMetadataDto(ArtworkMetadata.Type.MATERIAL, ArtworkMetadata.Status.PENDING)));
        artworkDto.addMediums(Collections.singletonList(utilArtworkMetadata.savedArtworkMetadataDto(ArtworkMetadata.Type.MEDIUM, ArtworkMetadata.Status.PENDING)));
        artworkDto.addStyles(Collections.singletonList(utilArtworkMetadata.savedArtworkMetadataDto(ArtworkMetadata.Type.STYLE, ArtworkMetadata.Status.PENDING)));
        artworkDto.setHistory(genArtworkHistory());
        artworkDto.addDocuments(Collections.singletonList(utilDocument.savedDocumentDto(FileCategory.IMAGE)));
        artworkDto.addDocuments(Collections.singletonList(utilDocument.savedDocumentDto(FileCategory.PRINCIPAL_IMAGE)));
        artworkDto.addDocuments(Collections.singletonList(utilDocument.savedDocumentDto(FileCategory.CERTIFICATE)));
        return artworkDto;
    }

    public SaveArtworkRequest genSaveArtworkRequest(ArtworkGroup.Status status, boolean artOwner, Long groupId) {
        var locationDto = utilLocation.savedLocationDto();
        return new SaveArtworkRequest(
                Arrays.asList(genMultipartFile(FileCategory.IMAGE.getSupportedFileExtensions().stream().findFirst().get())),    // images
                genMultipartFile(FileCategory.PRINCIPAL_IMAGE.getSupportedFileExtensions().stream().findFirst().get()),         // principalImage
                genUUID(),                                                                                                      // title
                new String[]{genUUID(), genUUID()},                                                                             // categories
                genUUID(),                                                                                                      // artist
                artOwner,                                                                                                       // iAmArtOwner
                2010,                                                                                                      // year
                new String[]{genUUID(), genUUID()},                                                                             // materials
                new String[]{genUUID(), genUUID()},                                                                             // mediums
                new String[]{genUUID(), genUUID()},                                                                             // styles
                genMultipartFile(FileCategory.CV.getSupportedFileExtensions().stream().findFirst().get()),                      // cv
                genMultipartFile(FileCategory.CERTIFICATE.getSupportedFileExtensions().stream().findFirst().get()),             // certificate
                new BigDecimal(100),                                                                                        // weight
                new BigDecimal(100),                                                                                        // height
                new BigDecimal(100),                                                                                        // width
                new BigDecimal(100),                                                                                        // depth
                genUUID(),                                                                                                      // address
                genUUID(),                                                                                                      // address2
                genUUID(),                                                                                                      // city
                locationDto.getCountry().getName(),                                                                             // country
                locationDto.getCountry().getRegion().getName(),                                                                 // region
                genUUID(),                                                                                                      // zipCode
                genUUID(),                                                                                                      // phone
                genUUID(),                                                                                                      // appraisalHistory
                genUUID(),                                                                                                      // locationHistory
                genUUID(),                                                                                                      // runsHistory
                genUUID(),                                                                                                      // maintenanceHistory
                genUUID(),                                                                                                      // notes
                status,                                                                                                         // status
                groupId                                                                                                         // groupId
        );
    }
}