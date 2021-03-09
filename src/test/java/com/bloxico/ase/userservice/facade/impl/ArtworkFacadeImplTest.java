package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.security.WithMockCustomUser;
import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.service.artwork.impl.ArtworkGroupServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private UtilArtwork utilArtwork;
    @Autowired private ArtworkGroupServiceImpl artworkGroupService;

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_groupNotFound() {
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, null);
        submitArtworkRequest.setGroupId(1024L);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_missingResume() {
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, false, null);
        submitArtworkRequest.setIAmArtOwner(true);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_missingCertificate() {
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, null);
        submitArtworkRequest.setIAmArtOwner(false);
        assertThrows(
                ArtworkException.class,
                () -> artworkFacade.submitArtwork(submitArtworkRequest));
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_createNewGroup() {
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.DRAFT, true, null);
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest);
        assertNotNull(saveArtworkResponse);
        assertNotNull(saveArtworkResponse.getGroupDto().getId());
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_saveToGroup_sameStatus() {
        var artworkGroupDto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.DRAFT, true, artworkGroupDto.getId());
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest);
        assertNotNull(saveArtworkResponse);
        assertSame(artworkGroupService.findGroupById(saveArtworkResponse.getGroupDto().getId()).getStatus(), ArtworkGroup.Status.DRAFT);
    }

    @Test
    @WithMockCustomUser(role = Role.ART_OWNER)
    public void submitArtwork_saveToGroup_updateStatus() {
        var artworkGroupDto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(ArtworkGroup.Status.WAITING_FOR_EVALUATION, true, artworkGroupDto.getId());
        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest);
        assertNotNull(saveArtworkResponse);
        assertSame(artworkGroupService.findGroupById(saveArtworkResponse.getGroupDto().getId()).getStatus(), ArtworkGroup.Status.WAITING_FOR_EVALUATION);
    }

}
