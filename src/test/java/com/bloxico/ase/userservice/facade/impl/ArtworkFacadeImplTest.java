package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTestWithAWS;
import com.bloxico.ase.testutil.UtilArtwork;
import org.springframework.beans.factory.annotation.Autowired;

public class ArtworkFacadeImplTest extends AbstractSpringTestWithAWS {

    @Autowired private ArtworkFacadeImpl artworkFacade;
    @Autowired private UtilArtwork utilArtwork;

//    @Test
//    @WithMockCustomUser(role = Role.ART_OWNER)
//    public void submitArtwork_missingResume() {
//        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(Artwork.Status.WAITING_FOR_EVALUATION, false);
//        submitArtworkRequest.setIAmArtOwner(true);
//        assertThrows(
//                ArtworkException.class,
//                () -> artworkFacade.submitArtwork(submitArtworkRequest));
//    }
//
//    @Test
//    @WithMockCustomUser(role = Role.ART_OWNER)
//    public void submitArtwork_missingCertificate() {
//        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(Artwork.Status.WAITING_FOR_EVALUATION, true);
//        submitArtworkRequest.setIAmArtOwner(false);
//        assertThrows(
//                ArtworkException.class,
//                () -> artworkFacade.submitArtwork(submitArtworkRequest));
//    }
//
//    @Test
//    @WithMockCustomUser(role = Role.ART_OWNER)
//    public void submitArtwork_createNewGroup() {
//        var submitArtworkRequest = utilArtwork.genSaveArtworkRequest(Artwork.Status.DRAFT, true);
//        var saveArtworkResponse = artworkFacade.submitArtwork(submitArtworkRequest);
//        assertNotNull(saveArtworkResponse);
//        assertNotNull(saveArtworkResponse.getArtworkDto().getId());
//    }

}
