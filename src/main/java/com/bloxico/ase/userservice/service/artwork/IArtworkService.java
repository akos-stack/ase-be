package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import org.springframework.data.domain.Page;

public interface IArtworkService {

    ArtworkDto saveArtwork(ArtworkDto artworkDto);

    ArtworkDto getArtworkById(Long id);

    Page<ArtworkDto> searchMyArtworks(Artwork.Status status, String title, int page, int size, String sort);
}
