package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkServiceImpl implements IArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkHistoryRepository artworkHistoryRepository;

    @Autowired
    public ArtworkServiceImpl(ArtworkRepository artworkRepository,
                              ArtworkHistoryRepository artworkHistoryRepository)
    {
        this.artworkRepository = artworkRepository;
        this.artworkHistoryRepository = artworkHistoryRepository;
    }

    @Override
    public ArtworkDto saveArtwork(ArtworkDto dto) {
        log.info("ArtworkServiceImpl.submitArtwork - start | dto: {}", dto);
        requireNonNull(dto);
        var artwork = MAPPER.toEntity(dto);
        artwork = artworkRepository.saveAndFlush(artwork);
        if (dto.getHistory() != null)
            saveArtworkHistory(artwork, dto);
        var artworkDto = MAPPER.toDto(artwork);
        log.info("ArtworkServiceImpl.submitArtwork - end | dto: {}", dto);
        return artworkDto;
    }

    private void saveArtworkHistory(Artwork artwork, ArtworkDto artworkDto) {
        var artworkHistory = MAPPER.toEntity(artworkDto.getHistory());
        artworkHistory.setArtwork(artwork);
        artworkHistoryRepository.save(artworkHistory);
    }

}
