package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_METADATA_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkMetadataServiceImpl implements IArtworkMetadataService {

    private final CategoryRepository categoryRepository;
    private final MediumRepository mediumRepository;
    private final MaterialRepository materialRepository;
    private final StyleRepository styleRepository;

    public ArtworkMetadataServiceImpl(CategoryRepository categoryRepository, MediumRepository mediumRepository, MaterialRepository materialRepository, StyleRepository styleRepository) {
        this.categoryRepository = categoryRepository;
        this.mediumRepository = mediumRepository;
        this.materialRepository = materialRepository;
        this.styleRepository = styleRepository;
    }

    @Override
    public ArtworkMetadataDto findOrSaveCategory(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.findOrSaveCategory - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var categoryDto = categoryRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveCategory(dto, principalId));
        log.debug("ArtworkMetadataServiceImpl.findOrSaveCategory - end | dto: {}, principalId: {}", dto, principalId);
        return categoryDto;
    }

    @Override
    public void updateCategoryStatus(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.updateCategoryStatus - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var category = categoryRepository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        category.setStatus(dto.getStatus());
        category.setUpdaterId(principalId);
        categoryRepository.saveAndFlush(category);
        log.debug("ArtworkMetadataServiceImpl.updateCategoryStatus - end | dto: {}, principalId: {}", dto, principalId);
    }

    @Override
    public void deleteCategory(String name) {
        log.debug("ArtworkMetadataServiceImpl.deleteCategory - start | name: {}", name);
        requireNonNull(name);
        var category = categoryRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        categoryRepository.delete(category);
        log.debug("ArtworkMetadataServiceImpl.deleteCategory - end | name: {}", name);
    }

    @Override
    public Page<ArtworkMetadataDto> fetchCategories(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.debug("ArtworkMetadataServiceImpl.fetchCategories - start | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var categoriesDto = categoryRepository
                .fetchCategories(status, name, pageable)
                .map(MAPPER::toDto);
        log.debug("ArtworkMetadataServiceImpl.fetchCategories - end | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        return categoriesDto;
    }

    @Override
    public List<ArtworkMetadataDto> fetchApprovedCategories(String name) {
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedCategories - start");
        List<ArtworkMetadataDto> categoriesDto = categoryRepository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name: "").stream().map(MAPPER::toDto).collect(Collectors.toList());
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedCategories - end");
        return categoriesDto;
    }

    @Override
    public ArtworkMetadataDto findOrSaveMaterial(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.findOrSaveMaterial - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var materialDto = materialRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveMaterial(dto, principalId));
        log.debug("ArtworkMetadataServiceImpl.findOrSaveMaterial - end | dto: {}, principalId: {}", dto, principalId);
        return materialDto;
    }

    @Override
    public void updateMaterialStatus(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.updateMaterialStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var material = materialRepository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        material.setStatus(dto.getStatus());
        material.setUpdaterId(principalId);
        materialRepository.saveAndFlush(material);
        log.debug("ArtworkMetadataServiceImpl.updateMaterialStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
    }

    @Override
    public void deleteMaterial(String name) {
        log.debug("ArtworkMetadataServiceImpl.deleteMaterial - start | name: {}", name);
        requireNonNull(name);
        var material = materialRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        materialRepository.delete(material);
        log.debug("ArtworkMetadataServiceImpl.deleteMaterial - end | name: {}", name);
    }

    @Override
    public Page<ArtworkMetadataDto> fetchMaterials(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.debug("ArtworkMetadataServiceImpl.fetchMaterials - start | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var materialsDto = materialRepository
                .fetchMaterials(status, name, pageable)
                .map(MAPPER::toDto);
        log.debug("ArtworkMetadataServiceImpl.fetchMaterials - end | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        return materialsDto;
    }

    @Override
    public List<ArtworkMetadataDto> fetchApprovedMaterials(String name) {
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedMaterials - start");
        List<ArtworkMetadataDto> materialsDto = materialRepository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name: "").stream().map(MAPPER::toDto).collect(Collectors.toList());
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedMaterials - end");
        return materialsDto;
    }

    @Override
    public ArtworkMetadataDto findOrSaveMedium(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.findOrSaveMedium - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var mediumDto = mediumRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveMedium(dto, principalId));
        log.debug("ArtworkMetadataServiceImpl.findOrSaveMedium - end | dto: {}, principalId: {}", dto, principalId);
        return mediumDto;
    }

    @Override
    public void updateMediumStatus(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.updateMediumStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var medium = mediumRepository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        medium.setStatus(dto.getStatus());
        medium.setUpdaterId(principalId);
        mediumRepository.saveAndFlush(medium);
        log.debug("ArtworkMetadataServiceImpl.updateMediumStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
    }

    @Override
    public void deleteMedium(String name) {
        log.debug("ArtworkMetadataServiceImpl.deleteMedium - start | name: {}", name);
        requireNonNull(name);
        var medium = mediumRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        mediumRepository.delete(medium);
        log.debug("ArtworkMetadataServiceImpl.deleteMedium - end | name: {}", name);
    }

    @Override
    public Page<ArtworkMetadataDto> fetchMediums(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.debug("ArtworkMetadataServiceImpl.fetchMediums - start | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var mediumsDto = mediumRepository
                .fetchMediums(status, name, pageable)
                .map(MAPPER::toDto);
        log.debug("ArtworkMetadataServiceImpl.fetchMediums - end | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        return mediumsDto;
    }

    @Override
    public List<ArtworkMetadataDto> fetchApprovedMediums(String name) {
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedMediums - start");
        List<ArtworkMetadataDto> mediumsDto = mediumRepository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name: "").stream().map(MAPPER::toDto).collect(Collectors.toList());
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedMediums - end");
        return mediumsDto;
    }

    @Override
    public ArtworkMetadataDto findOrSaveStyle(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.findOrSaveStyle - start | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var styleDto = styleRepository
                .findByNameIgnoreCase(dto.getName())
                .map(MAPPER::toDto)
                .filter(dto::equals)
                .orElseGet(() -> saveStyle(dto, principalId));
        log.debug("ArtworkMetadataServiceImpl.findOrSaveStyle - end | dto: {}, principalId: {}", dto, principalId);
        return styleDto;
    }

    @Override
    public void updateStyleStatus(ArtworkMetadataDto dto, long principalId) {
        log.debug("ArtworkMetadataServiceImpl.updateStyleStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
        requireNonNull(dto);
        var style = styleRepository
                .findByNameIgnoreCase(dto.getName())
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        style.setStatus(dto.getStatus());
        style.setUpdaterId(principalId);
        styleRepository.saveAndFlush(style);
        log.debug("ArtworkMetadataServiceImpl.updateStyleStatus - updateMaterialStatus | dto: {}, principalId: {}", dto, principalId);
    }

    @Override
    public void deleteStyle(String name) {
        log.debug("ArtworkMetadataServiceImpl.deleteStyle - start | name: {}", name);
        requireNonNull(name);
        var style = styleRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(ARTWORK_METADATA_NOT_FOUND::newException);
        styleRepository.delete(style);
        log.debug("ArtworkMetadataServiceImpl.deleteStyle - end | name: {}", name);
    }

    @Override
    public Page<ArtworkMetadataDto> fetchStyles(ArtworkMetadataStatus status, String name, int page, int size, String sort) {
        log.debug("ArtworkMetadataServiceImpl.fetchStyles - start | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        var stylesDto = styleRepository
                .fetchStyles(status, name, pageable)
                .map(MAPPER::toDto);
        log.debug("ArtworkMetadataServiceImpl.fetchStyles - end | status: {}, name:{}, page: {}, size: {}, sort {}", status, name, page, size, sort);
        return stylesDto;
    }

    @Override
    public List<ArtworkMetadataDto> fetchApprovedStyles(String name) {
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedStyles - start");
        List<ArtworkMetadataDto> stylesDto = styleRepository
                .findAllByStatusAndNameContains(APPROVED, name != null ? name: "").stream().map(MAPPER::toDto).collect(Collectors.toList());
        log.debug("ArtworkMetadataServiceImpl.fetchApprovedStyles - end");
        return stylesDto;
    }

    private ArtworkMetadataDto saveCategory(ArtworkMetadataDto dto, long principalId) {
        var category = MAPPER.toCategoryEntity(dto);
        category.setCreatorId(principalId);
        return MAPPER.toDto(categoryRepository.saveAndFlush(category));
    }


    private ArtworkMetadataDto saveMaterial(ArtworkMetadataDto dto, long principalId) {
        var material = MAPPER.toMaterialEntity(dto);
        material.setCreatorId(principalId);
        return MAPPER.toDto(materialRepository.saveAndFlush(material));
    }


    private ArtworkMetadataDto saveMedium(ArtworkMetadataDto dto, long principalId) {
        var medium = MAPPER.toMediumEntity(dto);
        medium.setCreatorId(principalId);
        return MAPPER.toDto(mediumRepository.saveAndFlush(medium));
    }


    private ArtworkMetadataDto saveStyle(ArtworkMetadataDto dto, long principalId) {
        var style = MAPPER.toStyleEntity(dto);
        style.setCreatorId(principalId);
        return MAPPER.toDto(styleRepository.saveAndFlush(style));
    }
}
