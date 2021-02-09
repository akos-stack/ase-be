package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.model.artwork.SearchArtworkMetadataResponse;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Transactional
abstract class AbstractArtworkMetadataFacadeImpl implements IArtworkMetadataFacade {

    protected abstract IArtworkMetadataService getService(ArtworkMetadataType type);

    @Override
    public ArtworkMetadataDto createArtworkMetadata(ArtworkMetadataCreateRequest request, long principalId) {
        log.info("AbstractArtworkMetadataFacadeImpl.createArtworkMetadata - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = getService(request.getType()).findOrSaveArtworkMetadata(dto, principalId);
        log.info("AbstractArtworkMetadataFacadeImpl.createArtworkMetadata - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateArtworkMetadataStatus(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("AbstractArtworkMetadataFacadeImpl.updateArtworkMetadataStatus - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        getService(request.getType()).updateArtworkMetadataStatus(dto, principalId);
        log.info("AbstractArtworkMetadataFacadeImpl.updateArtworkMetadataStatus - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteArtworkMetadata(String name, ArtworkMetadataType type) {
        log.info("AbstractArtworkMetadataFacadeImpl.deleteArtworkMetadata - start | name: {}, type: {}", name, type);
        getService(type).deleteArtworkMetadata(name);
        log.info("AbstractArtworkMetadataFacadeImpl.deleteArtworkMetadata - end | name: {}, type: {}", name, type);
    }

    @Override
    public PagedArtworkMetadataResponse searchArtworkMetadata(ArtworkMetadataType type, ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("AbstractArtworkMetadataFacadeImpl.searchArtworkMetadata - start | type: {}, status: {}, page: {}, size: {}, sort: {}", type, status, page, size, sort);
        var pagedDto = getService(type).searchArtworkMetadata(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("AbstractArtworkMetadataFacadeImpl.searchArtworkMetadata - end | type: {}, status: {}, page: {}, size: {}, sort: {}", type, status, page, size, sort);
        return response;
    }

    @Override
    public SearchArtworkMetadataResponse searchApprovedArtworkMetadata(String name, ArtworkMetadataType type) {
        log.info("AbstractArtworkMetadataFacadeImpl.searchApprovedArtworkMetadata - start | name: {}, type: {}", name, type);
        var artworkMetadataDtos = getService(type).searchApprovedArtworkMetadata(name);
        var response = new SearchArtworkMetadataResponse(artworkMetadataDtos);
        log.info("AbstractArtworkMetadataFacadeImpl.searchApprovedArtworkMetadata - end | name: {}, type: {}", name, type);
        return response;
    }
}
