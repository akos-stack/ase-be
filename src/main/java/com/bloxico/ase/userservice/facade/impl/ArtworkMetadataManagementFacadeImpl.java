package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.facade.IArtworkMetadataManagementFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Slf4j
@Service
@Transactional
public class ArtworkMetadataManagementFacadeImpl implements IArtworkMetadataManagementFacade {

    private final IArtworkMetadataService artworkMetadataService;

    public ArtworkMetadataManagementFacadeImpl(IArtworkMetadataService artworkMetadataService) {
        this.artworkMetadataService = artworkMetadataService;
    }

    @Override
    public ArtworkMetadataDto createCategory(ArtworkMetadataCreateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.createCategory - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = artworkMetadataService.findOrSaveCategory(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.createCategory - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateCategory(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.updateCategory - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        artworkMetadataService.updateCategoryStatus(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.updateCategory - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteCategory(String name) {
        log.info("ArtworkMetadataManagementFacadeImpl.deleteCategory - start | name: {}", name);
        artworkMetadataService.deleteCategory(name);
        log.info("ArtworkMetadataManagementFacadeImpl.deleteCategory - end | name: {}", name);
    }

    @Override
    public PagedArtworkMetadataResponse fetchCategories(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("ArtworkMetadataManagementFacadeImpl.fetchCategories - start | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        var pagedDto = artworkMetadataService.fetchCategories(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("ArtworkMetadataManagementFacadeImpl.fetchCategories - end | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        return response;
    }

    @Override
    public ArtworkMetadataDto createMaterial(ArtworkMetadataCreateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = artworkMetadataService.findOrSaveMaterial(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateMaterial(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.updateMaterial - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        artworkMetadataService.updateMaterialStatus(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.updateMaterial - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteMaterial(String name) {
        log.info("ArtworkMetadataManagementFacadeImpl.deleteMaterial - start | name: {}", name);
        artworkMetadataService.deleteMaterial(name);
        log.info("ArtworkMetadataManagementFacadeImpl.deleteMaterial - end | name: {}", name);
    }

    @Override
    public PagedArtworkMetadataResponse fetchMaterials(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("ArtworkMetadataManagementFacadeImpl.fetchMaterials - start | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        var pagedDto = artworkMetadataService.fetchMaterials(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("ArtworkMetadataManagementFacadeImpl.fetchMaterials - end | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        return response;
    }

    @Override
    public ArtworkMetadataDto createMedium(ArtworkMetadataCreateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = artworkMetadataService.findOrSaveMedium(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateMedium(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.updateMedium - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        artworkMetadataService.updateMediumStatus(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.updateMedium - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteMedium(String name) {
        log.info("ArtworkMetadataManagementFacadeImpl.deleteMedium - start | name: {}", name);
        artworkMetadataService.deleteMedium(name);
        log.info("ArtworkMetadataManagementFacadeImpl.deleteMedium - end | name: {}", name);
    }

    @Override
    public PagedArtworkMetadataResponse fetchMediums(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("ArtworkMetadataManagementFacadeImpl.fetchMediums - start | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        var pagedDto = artworkMetadataService.fetchMediums(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("ArtworkMetadataManagementFacadeImpl.fetchMediums - end | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        return response;
    }

    @Override
    public ArtworkMetadataDto createStyle(ArtworkMetadataCreateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = artworkMetadataService.findOrSaveStyle(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.createMaterial - end | request: {}, principalId: {}", request, principalId);
        return response;
    }

    @Override
    public void updateStyle(ArtworkMetadataUpdateRequest request, long principalId) {
        log.info("ArtworkMetadataManagementFacadeImpl.updateStyle - start | request: {}, principalId: {}", request, principalId);
        var dto = MAPPER.toArtworkMetadataDto(request);
        artworkMetadataService.updateStyleStatus(dto, principalId);
        log.info("ArtworkMetadataManagementFacadeImpl.updateStyle - end | request: {}, principalId: {}", request, principalId);
    }

    @Override
    public void deleteStyle(String name) {
        log.info("ArtworkMetadataManagementFacadeImpl.deleteStyle - start | name: {}", name);
        artworkMetadataService.deleteStyle(name);
        log.info("ArtworkMetadataManagementFacadeImpl.deleteStyle - end | name: {}", name);
    }

    @Override
    public PagedArtworkMetadataResponse fetchStyles(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.info("ArtworkMetadataManagementFacadeImpl.fetchStyles - start | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        var pagedDto = artworkMetadataService.fetchStyles(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("ArtworkMetadataManagementFacadeImpl.fetchStyles - end | status: {}, page: {}, size: {}, sort: {}", status, page, size, sort);
        return response;
    }
}
