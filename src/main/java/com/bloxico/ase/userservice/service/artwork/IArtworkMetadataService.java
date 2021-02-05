package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IArtworkMetadataService {

    ArtworkMetadataDto findOrSaveArtworkMetadata(ArtworkMetadataDto dto, long principalId);

    void updateArtworkMetadataStatus(ArtworkMetadataDto dto, long principalId);

    void deleteArtworkMetadata(String name);

    Page<ArtworkMetadataDto> searchArtworkMetadata(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> searchApprovedArtworkMetadata(String name);
}
