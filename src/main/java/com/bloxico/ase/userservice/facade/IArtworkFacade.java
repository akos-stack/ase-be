package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkResponse;

public interface IArtworkFacade {

    SubmitArtworkResponse submitArtwork(SubmitArtworkRequest request, long principalId);
}
