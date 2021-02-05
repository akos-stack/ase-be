package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.*;
import com.bloxico.ase.userservice.repository.artwork.ArtworkMetadataRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_METADATA_NOT_FOUND;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_METADATA_TYPE_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
abstract class AbstractArtworkMetadataServiceImpl<T extends IArtworkMetadataEntity> implements IArtworkMetadataService {

    private final ArtworkMetadataRepository<T> repository;

    protected AbstractArtworkMetadataServiceImpl(ArtworkMetadataRepository<T> repository) {
        this.repository = repository;
    }

    protected abstract ArtworkMetadataType getType();

    @Override
    public ArtworkMetadataDto findOrSaveArtworkMetadata(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.findOrSaveCategory - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var artworkMetadataDto = repository
                .findByNameIgnoreCase(dto.getName())
                .map(this::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveArtworkMetadata(dto, principalId));
        log.debug("ArtworkMetadataServiceImpl.findOrSaveCategory - end | dto: {}, principalId: {}", dto, principalId);
        return artworkMetadataDto;
    }

    @Override
    public void updateArtworkMetadataStatus(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.updateCategoryStatus - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var artworkMetadata = repository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        artworkMetadata.setStatus(dto.getStatus());
        artworkMetadata.setUpdaterId(principalId);
        repository.saveAndFlush(artworkMetadata);
        log.debug("ArtworkMetadataServiceImpl.updateCategoryStatus - end | dto: {}, principalId: {}", dto, principalId);
    }

    @Override
    public void deleteArtworkMetadata(String name) {
        log.debug("ArtworkMetadataServiceImpl.deleteCategory - start | name: {}", name);
        requireNonNull(name);
        var artworkMetadata = repository
                .findByNameIgnoreCase(name)
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        repository.delete(artworkMetadata);
        log.debug("ArtworkMetadataServiceImpl.deleteCategory - end | name: {}", name);
    }

    @Override
    public Page<ArtworkMetadataDto> searchArtworkMetadata(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.debug("ArtworkMetadataServiceImpl.fetchCategories - start | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var artworkMetadataDtos = repository
                .search(status, name, pageable)
                .map(this::toDto);
        log.debug("ArtworkMetadataServiceImpl.fetchCategories - end | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        return artworkMetadataDtos;
    }

    @Override
    public List<ArtworkMetadataDto> searchApprovedArtworkMetadata(String name) {
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedCategories - start");
        List<ArtworkMetadataDto> artworkMetadataDtos = repository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name: "").stream().map(this::toDto).collect(Collectors.toList());
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedCategories - end");
        return artworkMetadataDtos;
    }
    private ArtworkMetadataDto saveArtworkMetadata(ArtworkMetadataDto dto, long principalId) {
        var artworkMetadata = toEntity(dto);
        artworkMetadata.setCreatorId(principalId);
        return toDto(repository.saveAndFlush(artworkMetadata));
    }

    private ArtworkMetadataDto toDto(T entity) {
        if ( entity == null ) {
            return null;
        }

        ArtworkMetadataDto artworkMetadataDto = new ArtworkMetadataDto();

        artworkMetadataDto.setId( entity.getId() );
        artworkMetadataDto.setName( entity.getName() );
        artworkMetadataDto.setStatus( entity.getStatus() );

        return artworkMetadataDto;
    }

    private T toEntity(ArtworkMetadataDto dto) {
        if ( dto == null ) {
            return null;
        }
        T artworkMetadataEntity;
        switch (getType()) {
            case CATEGORY:
                artworkMetadataEntity = (T) new Category();
                break;
            case MATERIAL:
                artworkMetadataEntity = (T) new Material();
                break;
            case MEDIUM:
                artworkMetadataEntity = (T) new Medium();
                break;
            case STYLE:
                artworkMetadataEntity = (T) new Style();
                break;
            default:
                throw ARTWORK_METADATA_TYPE_NOT_FOUND.newException();
        }

        artworkMetadataEntity.setId( dto.getId() );
        artworkMetadataEntity.setName( dto.getName() );
        artworkMetadataEntity.setStatus( dto.getStatus() );

        return artworkMetadataEntity;
    }
}
