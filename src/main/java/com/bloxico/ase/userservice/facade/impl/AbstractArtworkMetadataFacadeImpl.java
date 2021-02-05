package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
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
        log.info("ArtworkMetadataManagementFacadeImpl.createCategory - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = getService(request.getType()).findOrSaveArtworkMetadata(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.createCategory - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateArtworkMetadataStatus(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.updateCategory - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        getService(request.getType()).updateArtworkMetadataStatus(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.updateCategory - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteArtworkMetadata(String name, ArtworkMetadataType type) {
        log.info("ArtworkMetadataManagementFacadeImpl.deleteCategory - start | name: {}", name);
        getService(type).deleteArtworkMetadata(name);
        log.info("ArtworkMetadataManagementFacadeImpl.deleteCategory - end | name: {}", name);
    }

    @Override
    public PagedArtworkMetadataResponse searchArtworkMetadata(ArtworkMetadataType type, ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("ArtworkMetadataManagementFacadeImpl.fetchCategories - start | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        var pagedDto = getService(type).searchArtworkMetadata(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("ArtworkMetadataManagementFacadeImpl.fetchCategories - end | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        return response;
    }

    @Override
    public ArrayArtworkMetadataResponse searchApprovedArtworkMetadata(String name, ArtworkMetadataType type) {
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedCategories - start");
        var artworkMetadataDtos = getService(type).searchApprovedArtworkMetadata(name);
        var response = new ArrayArtworkMetadataResponse(artworkMetadataDtos);
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedCategories - end");
        return response;
    }
}
