package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkGroupServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private UtilUser utilUser;
    @Autowired private ArtworkGroupServiceImpl artworkGroupService;

    @Test
    public void submitArtwork_groupNotFound() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, null);
        submitArtworkRequest.setGroupId(1024L);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId()));
    }

    @Test
    public void submitArtwork_missingResume() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, null);
        submitArtworkRequest.setCv(null);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId()));
    }

    @Test
    public void submitArtwork_missingCertificate() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, null);
        submitArtworkRequest.setCertificate(null);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId()));
    }

    @Test
    public void submitArtwork_createNewGroup() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.DRAFT, true, null);
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId());
        assertNotNull(saveArtworkResponse);
        assertNotNull(saveArtworkResponse.getGroupDto().getId());
    }

    @Test
    public void submitArtwork_saveToGroup_sameStatus() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var artworkGroupDto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.DRAFT, true, artworkGroupDto.getId());
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId());
        assertNotNull(saveArtworkResponse);
        assertSame(artworkGroupService.findGroupById(saveArtworkResponse.getGroupDto().getId()).getStatus(), ArtworkGroup.Status.DRAFT);
    }

    @Test
    public void submitArtwork_saveToGroup_updateStatus() {
        var artOwnerDto = utilArtwork.savedArtOwnerDto();
        var artworkGroupDto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, artworkGroupDto.getId());
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest, artOwnerDto.getUserProfile().getUserId());
        assertNotNull(saveArtworkResponse);
        assertSame(artworkGroupService.findGroupById(saveArtworkResponse.getGroupDto().getId()).getStatus(), ArtworkGroup.Status.WAITING_FOR_EVALUATION);

    }

}
