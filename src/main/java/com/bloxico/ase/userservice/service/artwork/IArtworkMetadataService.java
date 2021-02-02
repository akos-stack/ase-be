package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IArtworkMetadataService {

    // CATEGORIES

    ArtworkMetadataDto findOrSaveCategory(ArtworkMetadataDto dto, long principalId);

    void updateCategoryStatus(ArtworkMetadataDto dto, long principalId);

    void deleteCategory(String name);

    Page<ArtworkMetadataDto> fetchCategories(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> fetchApprovedCategories();

    // MATERIALS

    ArtworkMetadataDto findOrSaveMaterial(ArtworkMetadataDto dto, long principalId);

    void updateMaterialStatus(ArtworkMetadataDto dto, long principalId);

    void deleteMaterial(String name);

    Page<ArtworkMetadataDto> fetchMaterials(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> fetchApprovedMaterials();

    // MEDIUMS

    ArtworkMetadataDto findOrSaveMedium(ArtworkMetadataDto dto, long principalId);

    void updateMediumStatus(ArtworkMetadataDto dto, long principalId);

    void deleteMedium(String name);

    Page<ArtworkMetadataDto> fetchMediums(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> fetchApprovedMediums();

    // STYLES

    ArtworkMetadataDto findOrSaveStyle(ArtworkMetadataDto dto, long principalId);

    void updateStyleStatus(ArtworkMetadataDto dto, long principalId);

    void deleteStyle(String name);

    Page<ArtworkMetadataDto> fetchStyles(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    List<ArtworkMetadataDto> fetchApprovedStyles();
}
