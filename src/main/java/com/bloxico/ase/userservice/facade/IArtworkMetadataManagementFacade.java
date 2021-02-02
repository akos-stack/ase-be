package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;

public interface IArtworkMetadataManagementFacade {

    ArtworkMetadataDto createCategory(ArtworkMetadataCreateRequest request, long principalId);

    void updateCategory(ArtworkMetadataUpdateRequest request, long principalId);

    void deleteCategory(String name);

    PagedArtworkMetadataResponse fetchCategories(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    ArtworkMetadataDto createMaterial(ArtworkMetadataCreateRequest request, long principalId);

    void updateMaterial(ArtworkMetadataUpdateRequest request, long principalId);

    void deleteMaterial(String name);

    PagedArtworkMetadataResponse fetchMaterials(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    ArtworkMetadataDto createMedium(ArtworkMetadataCreateRequest request, long principalId);

    void updateMedium(ArtworkMetadataUpdateRequest request, long principalId);

    void deleteMedium(String name);

    PagedArtworkMetadataResponse fetchMediums(ArtworkMetadataStatus status, String name, int page, int size, String sort);

    ArtworkMetadataDto createStyle(ArtworkMetadataCreateRequest request, long principalId);

    void updateStyle(ArtworkMetadataUpdateRequest request, long principalId);

    void deleteStyle(String name);

    PagedArtworkMetadataResponse fetchStyles(ArtworkMetadataStatus status, String name, int page, int size, String sort);
}
