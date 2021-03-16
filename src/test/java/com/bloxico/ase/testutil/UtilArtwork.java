package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.entity.artwork.Artist;
import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import com.bloxico.ase.userservice.facade.impl.ArtworkFacadeImpl;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkServiceImpl;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.UpdateArtworkDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.*;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.*;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.FileCategory.PRINCIPAL_IMAGE;
import static java.util.Objects.requireNonNull;

@Component
public class UtilArtwork {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilLocation utilLocation;
    @Autowired private UtilDocument utilDocument;
    @Autowired private UtilUserProfile utilUserProfile;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private ArtistRepository artistRepository;
    @Autowired private ArtworkServiceImpl artworkService;
    @Autowired private ArtworkFacadeImpl artworkFacade;

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

    public ArtworkDto genArtworkDto() {
        return genArtworkDto(randEnumConst(Status.class));
    }

    public ArtworkDto genArtworkDto(Status status) {
        return genArtworkDto(status, utilUserProfile.savedArtOwnerDto().getId());
    }

    public ArtworkDto genArtworkDto(Status status, long ownerId) {
        return genArtworkDto(status, ownerId, utilLocation.savedLocationDto().getId());
    }

    public ArtworkDto genArtworkDto(Status status, long ownerId, long locationId) {
        var artwork = new ArtworkDto();
        artwork.setTitle(genUUID());
        artwork.setYear(genPosInt(Year.now().getValue()));
        artwork.setWeight(genPosBigDecimal(20));
        artwork.setHeight(genPosBigDecimal(20));
        artwork.setWidth(genPosBigDecimal(20));
        artwork.setDepth(genPosBigDecimal(20));
        artwork.setPhone(genUUID());
        artwork.setStatus(status);
        artwork.setOwnerId(ownerId);
        artwork.setLocationId(locationId);
        artwork.setArtist(savedArtistDto());
        artwork.addCategories(utilArtworkMetadata.savedArtworkMetadataDtos(CATEGORY));
        artwork.addMaterials(utilArtworkMetadata.savedArtworkMetadataDtos(MATERIAL));
        artwork.addMediums(utilArtworkMetadata.savedArtworkMetadataDtos(MEDIUM));
        artwork.addStyles(utilArtworkMetadata.savedArtworkMetadataDtos(STYLE));
        artwork.setAppraisalHistory(genUUID());
        artwork.setLocationHistory(genUUID());
        artwork.setRunsHistory(genUUID());
        artwork.setMaintenanceHistory(genUUID());
        artwork.setNotes(genUUID());
        return artwork;
    }

    public ArtworkDto saved(ArtworkDto artworkDto) {
        return artworkService.saveArtwork(artworkDto);
    }

    public UpdateArtworkDataRequest genUpdateArtworkDataRequest(Status status) {
        return genUpdateArtworkDataRequest(saved(genArtworkDto()).getId(), status);
    }

    public UpdateArtworkDataRequest genUpdateArtworkDataRequest(long artworkId,
                                                                Status status)
    {
        return genUpdateArtworkDataRequest(
                artworkId, status, true,
                utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId());
    }

    public UpdateArtworkDataRequest genUpdateArtworkDataRequest(Status status,
                                                                boolean iAmArtOwner)
    {
        return genUpdateArtworkDataRequest(
                saved(genArtworkDto()).getId(), status, iAmArtOwner,
                utilDocument.savedDocumentDto(PRINCIPAL_IMAGE).getId());
    }

    public UpdateArtworkDataRequest genUpdateArtworkDataRequest(long artworkId,
                                                                Status status,
                                                                boolean iAmArtOwner,
                                                                long principalImageId)
    {
        var region = utilLocation.savedRegion();
        var country = utilLocation.savedCountryWithRegion(region);
        return new UpdateArtworkDataRequest(
                artworkId,                        // artworkId
                principalImageId,                 // principal_image_id
                genUUID(),                        // title
                List.of(genUUID(), genUUID()),    // categories
                genUUID(),                        // artist
                iAmArtOwner,                      // iAmArtOwner
                genPosInt(Year.now().getValue()), // year
                List.of(genUUID(), genUUID()),    // materials
                List.of(genUUID(), genUUID()),    // mediums
                List.of(genUUID(), genUUID()),    // styles
                genPosBigDecimal(100),            // weight
                genPosBigDecimal(100),            // height
                genPosBigDecimal(100),            // width
                genPosBigDecimal(100),            // depth
                genUUID(),                        // address
                genUUID(),                        // address2
                genUUID(),                        // city
                country.getName(),                // country
                region.getName(),                 // region
                genUUID(),                        // zipCode
                genUUID(),                        // phone
                genUUID(),                        // appraisalHistory
                genUUID(),                        // locationHistory
                genUUID(),                        // runsHistory
                genUUID(),                        // maintenanceHistory
                genUUID(),                        // notes
                status                            // status
        );
    }

    public Map<String, String> genSaveArtworkDocumentsFormParams(FileCategory fileCategory) {
        var map = new HashMap<String, String>();
        map.put("artworkId", String.valueOf(saved(genArtworkDto()).getId()));
        map.put("fileCategory", fileCategory.name());
        return map;
    }

    public Map<String, String> genSaveArtworkDocumentsFormParamsWithOwner(FileCategory fileCategory) {
        var map = new HashMap<String, String>();
        map.put("artworkId", String.valueOf(saved(genArtworkDto()).getId()));
        map.put("fileCategory", fileCategory.name());
        return map;
    }

    public long ownerIdOf(long artworkId) {
        return requireNonNull(
                artworkService
                        .findArtworkById(WithOwner.any(artworkId))
                        .getOwnerId());
    }

}
