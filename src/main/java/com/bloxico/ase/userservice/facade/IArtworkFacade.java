package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.*;

public interface IArtworkFacade {

    ArtworkResponse createArtworkDraft();

    DetailedArtworkResponse findArtworkById(WithOwner<FindByArtworkIdRequest> withOwner);

    DetailedArtworkResponse updateArtworkData(WithOwner<UpdateArtworkDataRequest> withOwner);

    SearchArtworkResponse searchArtworks(WithOwner<SearchArtworkRequest> withOwner, PageRequest page);

    void deleteArtwork(WithOwner<DeleteArtworkRequest> withOwner);

}
