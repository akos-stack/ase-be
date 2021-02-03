package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.Assert.assertEquals;

public class ArtworkMetadataManagementFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private ArtworkMetadataManagementFacadeImpl facade;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MediumRepository mediumRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Test
    public void createCategory() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createCategory(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateCategoryStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createCategory(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING);
        facade.updateCategory(updateRequest, admin.getId());

        assertEquals(categoryRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteCategory() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createCategory(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteCategory(request.getName());

        assertEquals(categoryRepository.findByNameIgnoreCase(request.getName()).isEmpty(),true);
    }

    @Test
    public void fetchCategories() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.fetchCategories(null, "", 0, 10, "name"));
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.fetchCategories(null, "", 0, 10, "name"));
    }

    @Test
    public void createMaterials() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMaterial(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateMaterialStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMaterial(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING);
        facade.updateMaterial(updateRequest, admin.getId());

        assertEquals(materialRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMaterial() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMaterial(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteMaterial(request.getName());

        assertEquals(materialRepository.findByNameIgnoreCase(request.getName()).isEmpty(),true);
    }

    @Test
    public void fetchMaterials() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.fetchMaterials(null, "", 0, 10, "name"));
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.fetchMaterials(null, "", 0, 10, "name"));
        var dto2 = mockUtil.savedMaterialDto();
    }

    @Test
    public void createMediums() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMedium(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateMediumStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMedium(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING);
        facade.updateMedium(updateRequest, admin.getId());

        assertEquals(mediumRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMedium() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createMedium(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteMedium(request.getName());

        assertEquals(mediumRepository.findByNameIgnoreCase(request.getName()).isEmpty(),true);
    }

    @Test
    public void fetchMediums() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.fetchMediums(null, "", 0, 10, "name"));
        var dto = mockUtil.savedMediumDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.fetchMediums(null, "", 0, 10, "name"));
    }

    @Test
    public void createStyle() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createStyle(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateStyleStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createStyle(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING);
        facade.updateStyle(updateRequest, admin.getId());

        assertEquals(styleRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteStyle() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid());
        var response = facade.createStyle(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteStyle(request.getName());

        assertEquals(styleRepository.findByNameIgnoreCase(request.getName()).isEmpty(),true);
    }

    @Test
    public void fetchStyles() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.fetchStyles(null, "", 0, 10, "name"));
        var dto = mockUtil.savedStyleDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.fetchStyles(null, "", 0, 10, "name"));
    }
}
