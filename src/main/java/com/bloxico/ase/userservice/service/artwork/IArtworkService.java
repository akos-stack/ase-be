package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;

public interface IArtworkService {

    ArtworkDto submitArtwork(ArtworkDto artworkDto, long principalId);
}
