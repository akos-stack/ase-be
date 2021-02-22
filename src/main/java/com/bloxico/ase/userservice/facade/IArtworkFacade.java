package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;

public interface IArtworkFacade {

    SaveArtworkResponse submitArtwork(SaveArtworkRequest request, long principalId);
}
