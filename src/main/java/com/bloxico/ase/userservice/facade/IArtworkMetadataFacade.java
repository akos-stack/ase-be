package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkMetadataResponse;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;

public interface IArtworkMetadataFacade {

    ArtworkMetadataDto createArtworkMetadata(ArtworkMetadataCreateRequest request, long principalId);

    void updateArtworkMetadataStatus(ArtworkMetadataUpdateRequest request, long principalId);

    void deleteArtworkMetadata(String name, ArtworkMetadataType type);

    PagedArtworkMetadataResponse searchArtworkMetadata(ArtworkMetadataType type, ArtworkMetadataStatus status, String name, int page, int size, String sort);

    SearchArtworkMetadataResponse searchApprovedArtworkMetadata(String name, ArtworkMetadataType type);
}
