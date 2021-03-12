package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import org.springframework.data.domain.Page;

public interface IArtworkService {

    ArtworkDto saveArtwork(ArtworkDto artworkDto);

    ArtworkDto getArtworkById(Long id);

    Page<ArtworkDto> searchArtworks(SearchArtworkRequest request, PageRequest page);

    void deleteArtworkById(Long id);
}
