package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkHistoryDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_NOT_FOUND;
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
        var artworkHistoryDto = saveArtworkHistory(artworkDto.getArtworkHistory(), artwork);
        var response = MAPPER.toDto(artwork);
        response.setArtworkHistory(artworkHistoryDto);
        log.info("ArtworkServiceImpl.submitArtwork - end | artworkDto: {}", artworkDto);
        return response;
    }

    @Override
    public ArtworkDto getArtworkById(Long id) {
        log.info("ArtworkServiceImpl.getArtwork - start | id: {}", id);
        requireNonNull(id);
        var artworkDto = artworkRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(ARTWORK_NOT_FOUND::newException);
        var artworkHistoryDto = artworkHistoryRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElse(null);
        artworkDto.setArtworkHistory(artworkHistoryDto);
        log.info("ArtworkServiceImpl.getArtwork - end | id: {}", id);
        return artworkDto;
    }

    @Override
    public Page<ArtworkDto> searchMyArtworks(Artwork.Status status, String title, int page, int size, String sort) {
        log.info("ArtworkServiceImpl.searchMyArtworks - start | status: {}, title: {}, page: {}, size: {}, sort: {}", status, title, page, size, sort);
        var pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var artworkDtos = artworkRepository
                .search(status, title, pageable)
                .map(MAPPER::toDto);
        log.info("ArtworkServiceImpl.searchMyArtworks - end | status: {}, title: {}, page: {}, size: {}, sort: {}", status, title, page, size, sort);
        return artworkDtos;
    }

    private ArtworkHistoryDto saveArtworkHistory(ArtworkHistoryDto artworkHistoryDto, Artwork artwork) {
        if(artworkHistoryDto != null) {
            artworkHistoryDto.setId(artwork.getId());
            var artworkHistory = artworkHistoryRepository.save(MAPPER.toEntity(artworkHistoryDto));
            artworkHistoryDto = MAPPER.toDto(artworkHistory);
        }
        return artworkHistoryDto;
    }
}
