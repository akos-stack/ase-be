package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.config.security.AseSecurityContextService;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkDto;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkHistoryDto;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.repository.artwork.ArtworkHistoryRepository;
import com.bloxico.ase.userservice.repository.artwork.ArtworkRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkService;
import com.bloxico.ase.userservice.web.model.PageRequest;
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
    private final ArtworkHistoryRepository artworkHistoryRepository;
    private final AseSecurityContextService securityContextService;

    @Autowired
    public ArtworkServiceImpl(ArtworkRepository artworkRepository,
                              ArtworkHistoryRepository artworkHistoryRepository, AseSecurityContextService securityContextService) {
        this.artworkRepository = artworkRepository;
        this.artworkHistoryRepository = artworkHistoryRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public ArtworkDto saveArtwork(ArtworkDto dto) {
        log.info("ArtworkServiceImpl.submitArtwork - start | dto: {}", dto);
        requireNonNull(dto);
        var artwork = MAPPER.toEntity(dto);
        artwork = artworkRepository.saveAndFlush(artwork);
        var artworkHistoryDto = saveArtworkHistory(dto.getArtworkHistory(), artwork);
        var response = MAPPER.toDto(artwork);
        response.setArtworkHistory(artworkHistoryDto);
        log.info("ArtworkServiceImpl.submitArtwork - end | artworkDto: {}", dto);
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
    public Page<ArtworkDto> searchArtworks(SearchArtworkRequest request, PageRequest page) {
        log.debug("ArtworkServiceImpl.searchMyArtworks - start | request: {}, page: {}", request, page);
        requireNonNull(request);
        requireNonNull(page);
        var pageable = page.toPageable();
        var ownerId = securityContextService.isAdmin() ? null : securityContextService.getArtOwner().getId();
        var artworkMetadataDtos = artworkRepository
                .search(request.getStatus(), request.getTitle(), ownerId, pageable)
                .map(MAPPER::toDto);
        log.debug("ArtworkServiceImpl.searchMyArtworks - end | request: {}, page: {}", request, page);
        return artworkMetadataDtos;
    }

    @Override
    public void deleteArtworkById(Long id) {
        log.info("ArtworkServiceImpl.deleteArtworkById - start | id: {}", id);
        requireNonNull(id);
        if(artworkHistoryRepository.existsById(id)) artworkHistoryRepository.deleteById(id);
        artworkRepository.deleteById(id);
        log.info("ArtworkServiceImpl.deleteArtworkById - end | id: {}", id);
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
