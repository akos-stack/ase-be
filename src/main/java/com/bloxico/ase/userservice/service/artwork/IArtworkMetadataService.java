package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SearchApprovedArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SearchArtworkMetadataRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IArtworkMetadataService {

    ArtworkMetadataDto findOrSaveArtworkMetadata(ArtworkMetadataDto dto);

    ArtworkMetadataDto updateArtworkMetadata(ArtworkMetadataDto dto);

    void deleteArtworkMetadata(String name);

    Page<ArtworkMetadataDto> searchArtworkMetadata(SearchArtworkMetadataRequest request, PageRequest page);

    List<ArtworkMetadataDto> searchApprovedArtworkMetadata(SearchApprovedArtworkMetadataRequest request);

}
