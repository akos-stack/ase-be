package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.repository.artwork.metadata.ArtworkMetadataRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.web.model.PageRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SearchApprovedArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SearchArtworkMetadataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
import static com.bloxico.ase.userservice.util.Functions.ifNull;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_METADATA_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Slf4j
abstract class AbstractArtworkMetadataServiceImpl<T extends ArtworkMetadata> implements IArtworkMetadataService {

    private final ArtworkMetadataRepository<T> repository;

    protected AbstractArtworkMetadataServiceImpl(ArtworkMetadataRepository<T> repository) {
        this.repository = repository;
    }

    protected abstract Type getType();

    @Override
    public ArtworkMetadataDto findOrSaveArtworkMetadata(ArtworkMetadataDto dto) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].findOrSaveArtworkMetadata - start | dto: {}",
                getType(), dto);
        requireNonNull(dto);
        var artworkMetadataDto = repository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .orElseGet(() -> saveArtworkMetadata(dto));
        log.debug("AbstractArtworkMetadataServiceImpl[{}].findOrSaveArtworkMetadata - end | dto: {}",
                getType(), dto);
        return artworkMetadataDto;
    }

    @Override
    public ArtworkMetadataDto updateArtworkMetadata(ArtworkMetadataDto dto) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].updateArtworkMetadata - start | dto: {}",
                getType(), dto);
        requireNonNull(dto);
        var artworkMetadata = repository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        artworkMetadata.setStatus(dto.getStatus());
        artworkMetadata = repository.saveAndFlush(artworkMetadata);
        var artworkMetadataDto = MAPPER.toDto(artworkMetadata);
        log.debug("AbstractArtworkMetadataServiceImpl[{}].updateArtworkMetadata - end | dto: {}",
                getType(), dto);
        return artworkMetadataDto;
    }

    @Override
    public void deleteArtworkMetadata(String name) {
        log.debug("ArtworkMetadataServiceImpl[{}].deleteArtworkMetadata - start | name: {}", getType(), name);
        requireNonNull(name);
        repository
                .findByNameIgnoreCase(name)
                .map(doto(repository::delete))
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        log.debug("ArtworkMetadataServiceImpl[{}].deleteArtworkMetadata - end | name: {}", getType(), name);
    }

    @Override
    public Page<ArtworkMetadataDto> searchArtworkMetadata(SearchArtworkMetadataRequest request, PageRequest page) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchArtworkMetadata - start | request: {}, page: {}", getType(), request, page);
        requireNonNull(request);
        requireNonNull(page);
        var artworkMetadataDtos = repository
                .search(request.getStatus(), request.getName(), page.toPageable())
                .map(MAPPER::toDto);
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchArtworkMetadata - end | request: {}, page: {}", getType(), request, page);
        return artworkMetadataDtos;
    }

    @Override
    public List<ArtworkMetadataDto> searchApprovedArtworkMetadata(SearchApprovedArtworkMetadataRequest request) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchApprovedArtworkMetadata - start | request: {}", getType(), request);
        requireNonNull(request);
        var artworkMetadataDtos = repository
                .findAllByStatusAndNameContains(APPROVED, ifNull(request.getName(), ""))
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchApprovedArtworkMetadata - end | request: {}", getType(), request);
        return artworkMetadataDtos;
    }

    private ArtworkMetadataDto saveArtworkMetadata(ArtworkMetadataDto dto) {
        T entity = getType().newInstance();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        return MAPPER.toDto(repository.saveAndFlush(entity));
    }

}
