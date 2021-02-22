package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IArtworkMetadataService {

    ArtworkMetadataDto findOrSaveArtworkMetadata(ArtworkMetadataDto dto, long principalId);

    ArtworkMetadataDto updateArtworkMetadata(ArtworkMetadataDto dto, long principalId);

    void deleteArtworkMetadata(String name);

    Page<ArtworkMetadataDto> searchArtworkMetadata(Status status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> searchApprovedArtworkMetadata(String name);

}
