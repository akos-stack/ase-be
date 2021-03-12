package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.*;

public interface IArtworkMetadataFacade {

    ArtworkMetadataDto saveArtworkMetadata(SaveArtworkMetadataRequest request);

    ArtworkMetadataDto updateArtworkMetadata(UpdateArtworkMetadataRequest request);

    void deleteArtworkMetadata(DeleteArtworkMetadataRequest request);

    SearchArtworkMetadataResponse searchArtworkMetadata(SearchArtworkMetadataRequest request, PageRequest page);

    SearchApprovedArtworkMetadataResponse searchApprovedArtworkMetadata(SearchApprovedArtworkMetadataRequest request);

}
