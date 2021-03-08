package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
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
    private final ArtworkHistoryRepository artworkHistoryRepository;

    public ArtworkServiceImpl(ArtworkRepository artworkRepository, ArtworkHistoryRepository artworkHistoryRepository) {
        this.artworkRepository = artworkRepository;
        this.artworkHistoryRepository = artworkHistoryRepository;
    }

    @Override
    public ArtworkDto saveArtwork(ArtworkDto artworkDto) {
        log.info("ArtworkServiceImpl.submitArtwork - start | artworkDto: {}", artworkDto);
        requireNonNull(artworkDto);
        var artwork = MAPPER.toEntity(artworkDto);
        artwork = artworkRepository.saveAndFlush(artwork);
        if(artworkDto.getHistory() != null) saveArtworkHistory(artwork, artworkDto);
        log.info("ArtworkServiceImpl.submitArtwork - end | artworkDto: {}", artworkDto);
        return MAPPER.toDto(artwork);
    }

    private void saveArtworkHistory(Artwork artwork, ArtworkDto artworkDto) {
        var artworkHistory = MAPPER.toEntity(artworkDto.getHistory());
        artworkHistory.setArtwork(artwork);
        artworkHistoryRepository.save(artworkHistory);
    }
}
