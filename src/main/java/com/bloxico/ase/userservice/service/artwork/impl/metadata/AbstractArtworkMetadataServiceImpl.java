package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.repository.artwork.metadata.ArtworkMetadataRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import java.util.List;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.util.Functions.doto;
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
    public Page<ArtworkMetadataDto> searchArtworkMetadata(Status status, String name, int page, int size, String sort) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchArtworkMetadata - start | status: {}, name:{}, page: {}, size: {}, sort {}",
                getType(), status, name, page, size, sort);
        var pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var artworkMetadataDtos = repository
                .search(status, name, pageable)
                .map(MAPPER::toDto);
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchArtworkMetadata - end | status: {}, name:{}, page: {}, size: {}, sort {}",
                getType(), status, name, page, size, sort);
        return artworkMetadataDtos;
    }

    @Override
    public List<ArtworkMetadataDto> searchApprovedArtworkMetadata(String name) {
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchApprovedArtworkMetadata - start | name: {}", getType(), name);
        var artworkMetadataDtos = repository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name : "")
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
        log.debug("AbstractArtworkMetadataServiceImpl[{}].searchApprovedArtworkMetadata - end | name: {}", getType(), name);
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
