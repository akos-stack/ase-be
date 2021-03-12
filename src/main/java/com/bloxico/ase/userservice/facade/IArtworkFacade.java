package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkResponse;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;

public interface IArtworkFacade {

    SaveArtworkResponse getArtworkById(Long id);

    SaveArtworkResponse saveArtworkDraft();

    SaveArtworkResponse saveArtworkDocuments(SaveArtworkDocumentsRequest request);

    SaveArtworkResponse saveArtworkData(SaveArtworkDataRequest request);

    PagedArtworkResponse searchMyArtworks(Artwork.Status status, String title, int page, int size, String sort);
}
