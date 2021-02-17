package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.service.artwork.IArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtistService implements IArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public ArtistDto saveArtist(ArtistDto artistDto, long principalId) {
        log.info("ArtistService.saveArtist - start | artistDto: {}, principalId: {} ", artistDto, principalId);
        requireNonNull(artistDto);
        var artist = MAPPER.toEntity(artistDto);
        artist.setCreatorId(principalId);
        log.info("ArtistService.saveArtist - end | artistDto: {}, principalId: {} ", artistDto, principalId);
        return MAPPER.toDto(artistRepository.save(artist));
    }
}
