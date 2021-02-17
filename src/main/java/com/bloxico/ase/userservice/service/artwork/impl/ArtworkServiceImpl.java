package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkServiceImpl implements IArtworkService {

    private final ArtworkRepository artworkRepository;

    public ArtworkServiceImpl(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    @Override
    public ArtworkDto submitArtwork(ArtworkDto artworkDto, long principalId) {
        log.info("ArtworkServiceImpl.submitArtwork - start | artworkDto: {}, principalId: {} ", artworkDto, principalId);
        requireNonNull(artworkDto);
        var artwork = artworkRepository.save(MAPPER.toEntity(artworkDto));
        log.info("ArtworkServiceImpl.submitArtwork - end | artworkDto: {}, principalId: {} ", artworkDto, principalId);
        return MAPPER.toDto(artwork);
    }

}
