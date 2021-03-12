package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkResponse;

public interface IArtworkFacade {

    SaveArtworkResponse getArtworkById(Long id);

    SaveArtworkResponse saveArtworkDraft();

    SaveArtworkResponse saveArtworkData(SaveArtworkDataRequest request);

    SearchArtworkResponse searchArtworks(SearchArtworkRequest request, PageRequest page);

    void deleteArtwork(Long id);
}
