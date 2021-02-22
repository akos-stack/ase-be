package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.web.model.artwork.*;

public interface IArtworkMetadataFacade {

    ArtworkMetadataDto saveArtworkMetadata(SaveArtworkMetadataRequest request, long principalId);

    ArtworkMetadataDto updateArtworkMetadata(UpdateArtworkMetadataRequest request, long principalId);

    void deleteArtworkMetadata(String name, Type type);

    PagedArtworkMetadataResponse searchArtworkMetadata(Type type, Status status, String name, int page, int size, String sort);

    SearchArtworkMetadataResponse searchApprovedArtworkMetadata(String name, Type type);

}
