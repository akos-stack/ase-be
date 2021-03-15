package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import org.springframework.data.domain.Page;

public interface IArtworkService {

    ArtworkDto saveArtwork(ArtworkDto artworkDto);

    ArtworkDto findArtworkById(WithOwner<Long> withOwner);

    Page<ArtworkDto> searchArtworks(WithOwner<SearchArtworkRequest> withOwner, PageRequest page);

    void deleteArtworkById(long artworkId);

}
