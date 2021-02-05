package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import com.bloxico.ase.userservice.web.model.artwork.PagedArtworkMetadataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtworkMetadataFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private AbstractArtworkMetadataFacadeImpl facade;

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
        var request = new ArtworkMetadataCreateRequest(uuid(), CATEGORY);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateCategoryStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), CATEGORY);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING, CATEGORY);
        facade.updateArtworkMetadataStatus(updateRequest, admin.getId());

        assertEquals(categoryRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteCategory() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), CATEGORY);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteArtworkMetadata(request.getName(), CATEGORY);

        assertTrue(categoryRepository.findByNameIgnoreCase(request.getName()).isEmpty());
    }

    @Test
    public void fetchCategories() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.searchArtworkMetadata(CATEGORY, null, "", 0, 10, "name"));
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.searchArtworkMetadata(CATEGORY, null, "", 0, 10, "name"));
    }

    @Test
    public void createMaterials() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MATERIAL);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateMaterialStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MATERIAL);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING, MATERIAL);
        facade.updateArtworkMetadataStatus(updateRequest, admin.getId());

        assertEquals(materialRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMaterial() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MATERIAL);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteArtworkMetadata(request.getName(), MATERIAL);

        assertTrue(materialRepository.findByNameIgnoreCase(request.getName()).isEmpty());
    }

    @Test
    public void fetchMaterials() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.searchArtworkMetadata(MATERIAL, null, "", 0, 10, "name"));
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.searchArtworkMetadata(MATERIAL, null, "", 0, 10, "name"));
    }

    @Test
    public void createMediums() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MEDIUM);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateMediumStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MEDIUM);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING, MEDIUM);
        facade.updateArtworkMetadataStatus(updateRequest, admin.getId());

        assertEquals(mediumRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMedium() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), MEDIUM);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteArtworkMetadata(request.getName(), MEDIUM);

        assertTrue(mediumRepository.findByNameIgnoreCase(request.getName()).isEmpty());
    }

    @Test
    public void fetchMediums() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.searchArtworkMetadata(MEDIUM, null, "", 0, 10, "name"));
        var dto = mockUtil.savedMediumDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.searchArtworkMetadata(MEDIUM, null, "", 0, 10, "name"));
    }

    @Test
    public void createStyle() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), STYLE);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getName(), response.getName());
    }

    @Test
    public void updateStyleStatus() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), STYLE);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        var updateRequest = new ArtworkMetadataUpdateRequest(request.getName(), ArtworkMetadataStatus.PENDING, STYLE);
        facade.updateArtworkMetadataStatus(updateRequest, admin.getId());

        assertEquals(styleRepository.findByNameIgnoreCase(request.getName()).get().getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteStyle() {
        var admin = mockUtil.savedAdmin();
        var request = new ArtworkMetadataCreateRequest(uuid(), STYLE);
        var response = facade.createArtworkMetadata(request, admin.getId());
        assertEquals(request.getStatus(), response.getStatus());

        facade.deleteArtworkMetadata(request.getName(), STYLE);

        assertTrue(styleRepository.findByNameIgnoreCase(request.getName()).isEmpty());
    }

    @Test
    public void fetchStyles() {
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(), 0, 0, 0),
                facade.searchArtworkMetadata(STYLE, null, "", 0, 10, "name"));
        var dto = mockUtil.savedStyleDto();
        assertEquals(
                new PagedArtworkMetadataResponse(List.of(dto), 1, 1, 1),
                facade.searchArtworkMetadata(STYLE, null, "", 0, 10, "name"));
    }

        @Test
    public void findApprovedCategories() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.searchApprovedArtworkMetadata("", CATEGORY));
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.searchApprovedArtworkMetadata("", CATEGORY));
        var dto2 = mockUtil.savedCategoryDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.searchApprovedArtworkMetadata("", CATEGORY));
    }

    @Test
    public void findApprovedMaterials() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.searchApprovedArtworkMetadata("", MATERIAL));
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.searchApprovedArtworkMetadata("", MATERIAL));
        var dto2 = mockUtil.savedMaterialDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.searchApprovedArtworkMetadata("", MATERIAL));
    }

    @Test
    public void findApprovedMediums() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.searchApprovedArtworkMetadata("", MEDIUM));
        var dto = mockUtil.savedMediumDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.searchApprovedArtworkMetadata("", MEDIUM));
        var dto2 = mockUtil.savedMediumDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.searchApprovedArtworkMetadata("", MEDIUM));
    }

    @Test
    public void findApprovedStyles() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.searchApprovedArtworkMetadata("", STYLE));
        var dto = mockUtil.savedStyleDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.searchApprovedArtworkMetadata("", STYLE));
        var dto2 = mockUtil.savedStyleDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.searchApprovedArtworkMetadata("", STYLE));
    }
}
