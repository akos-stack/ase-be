package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkServiceImpl implements IArtworkService {

    private final ArtworkRepository artworkRepository;

    @Autowired
    public ArtworkServiceImpl(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    @Override
    public ArtworkDto saveArtwork(ArtworkDto dto) {
        log.info("ArtworkServiceImpl.saveArtwork - start | dto: {}", dto);
        requireNonNull(dto);
        var artwork = MAPPER.toEntity(dto);
        artwork = artworkRepository.saveAndFlush(artwork);
        var artworkDto = MAPPER.toDto(artwork);
        log.info("ArtworkServiceImpl.saveArtwork - end | artworkDto: {}", dto);
        return artworkDto;
    }

    @Override
    public ArtworkDto findArtworkById(WithOwner<Long> withOwner) {
        log.info("ArtworkServiceImpl.getArtworkById - start | withOwner: {}", withOwner);
        var artworkDto = artworkRepository
                .findByIdForOwner(withOwner.getRequest(), withOwner.getOwner())
                .map(MAPPER::toDto)
                .orElseThrow(ARTWORK_NOT_FOUND::newException);
        log.info("ArtworkServiceImpl.getArtworkById - end | withOwner: {}", withOwner);
        return artworkDto;
    }

    @Override
    public Page<ArtworkDto> searchArtworks(WithOwner<SearchArtworkRequest> withOwner, PageRequest page) {
        log.debug("ArtworkServiceImpl.searchArtworks - start | request: {}, page: {}", withOwner, page);
        requireNonNull(withOwner);
        requireNonNull(page);
        var request = withOwner.getRequest();
        var artworkMetadataDtos = artworkRepository
                .search(request.getStatus(),
                        request.getTitle(),
                        withOwner.getOwner(),
                        page.toPageable())
                .map(MAPPER::toDto);
        log.debug("ArtworkServiceImpl.searchArtworks - end | request: {}, page: {}", withOwner, page);
        return artworkMetadataDtos;
    }

    @Override
    public void deleteArtworkById(long id) {
        log.info("ArtworkServiceImpl.deleteArtworkById - start | id: {}", id);
        artworkRepository.deleteById(id);
        log.info("ArtworkServiceImpl.deleteArtworkById - end | id: {}", id);
    }

}
