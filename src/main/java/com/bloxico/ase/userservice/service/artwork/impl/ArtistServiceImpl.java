package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtistDto;
import com.bloxico.ase.userservice.repository.artwork.ArtistRepository;
import com.bloxico.ase.userservice.service.artwork.IArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtistServiceImpl implements IArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public ArtistDto saveArtist(ArtistDto dto) {
        log.info("ArtistService.saveArtist - start | dto: {}", dto);
        requireNonNull(dto);
        var artist = MAPPER.toEntity(dto);
        artist = artistRepository.saveAndFlush(artist);
        var artistDto = MAPPER.toDto(artist);
        log.info("ArtistService.saveArtist - end | dto: {}", dto);
        return artistDto;
    }

}
