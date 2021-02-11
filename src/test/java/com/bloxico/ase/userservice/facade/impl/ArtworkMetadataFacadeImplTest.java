package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtworkMetadata;
import com.bloxico.ase.testutil.UtilUser;
import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataCreateRequest;
import com.bloxico.ase.userservice.web.model.artwork.ArtworkMetadataUpdateRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.util.ArtworkMetadataType.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

public class ArtworkMetadataFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private AbstractArtworkMetadataFacadeImpl facade;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private MediumRepository mediumRepository;
    @Autowired private StyleRepository styleRepository;

    @Test
    public void createCategory() {
        var admin = utilUser.savedAdmin();
        var name = genUUID();
        var request = new ArtworkMetadataCreateRequest(name, CATEGORY);
        facade.createArtworkMetadata(request, admin.getId());

        var newlyCreated = categoryRepository.findByNameIgnoreCase(name).orElseThrow();
        assertEquals(newlyCreated.getName(), name);
        assertEquals(newlyCreated.getStatus(), APPROVED);
    }

    @Test
    public void updateCategoryStatus() {
        var savedCategory = utilArtworkMetadata.savedCategory(APPROVED);
        var updateRequest = new ArtworkMetadataUpdateRequest(savedCategory.getName(), ArtworkMetadataStatus.PENDING, CATEGORY);
        facade.updateArtworkMetadataStatus(updateRequest, savedCategory.getCreatorId());

        var updated = categoryRepository.findByNameIgnoreCase(savedCategory.getName()).orElseThrow();
        assertEquals(updated.getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteCategory() {
        var savedCategory = utilArtworkMetadata.savedCategory(APPROVED);
        facade.deleteArtworkMetadata(savedCategory.getName(), CATEGORY);

        assertTrue(categoryRepository.findByNameIgnoreCase(savedCategory.getName()).isEmpty());
    }

    @Test
    public void fetchCategories() {
        assertThat(
                facade.searchArtworkMetadata(CATEGORY, null, "", 0, 10, "name").getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedCategoryDto(APPROVED);
        assertThat(
                facade.searchArtworkMetadata(CATEGORY, null, "", 0, 10, "name").getEntries(),
                hasItems(dto));
    }

    @Test
    public void createMaterials() {
        var admin = utilUser.savedAdmin();
        var name = genUUID();
        var request = new ArtworkMetadataCreateRequest(name, MATERIAL);
        facade.createArtworkMetadata(request, admin.getId());

        var newlyCreated = materialRepository.findByNameIgnoreCase(name).orElseThrow();
        assertEquals(newlyCreated.getName(), name);
        assertEquals(newlyCreated.getStatus(), APPROVED);
    }

    @Test
    public void updateMaterialStatus() {
        var savedMaterial = utilArtworkMetadata.savedMaterial(APPROVED);
        var updateRequest = new ArtworkMetadataUpdateRequest(savedMaterial.getName(), ArtworkMetadataStatus.PENDING, MATERIAL);
        facade.updateArtworkMetadataStatus(updateRequest, savedMaterial.getCreatorId());

        var updated = materialRepository.findByNameIgnoreCase(savedMaterial.getName()).orElseThrow();
        assertEquals(updated.getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMaterial() {
        var savedMaterial = utilArtworkMetadata.savedMaterial(APPROVED);
        facade.deleteArtworkMetadata(savedMaterial.getName(), MATERIAL);

        assertTrue(materialRepository.findByNameIgnoreCase(savedMaterial.getName()).isEmpty());
    }

    @Test
    public void fetchMaterials() {
        assertThat(
                facade.searchArtworkMetadata(MATERIAL, null, "", 0, 10, "name").getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedMaterialDto();
        assertThat(
                facade.searchArtworkMetadata(MATERIAL, null, "", 0, 10, "name").getEntries(),
                hasItems(dto));
    }

    @Test
    public void createMediums() {
        var admin = utilUser.savedAdmin();
        var name = genUUID();
        var request = new ArtworkMetadataCreateRequest(name, MEDIUM);
        facade.createArtworkMetadata(request, admin.getId());

        var newlyCreated = mediumRepository.findByNameIgnoreCase(name).orElseThrow();
        assertEquals(newlyCreated.getName(), name);
        assertEquals(newlyCreated.getStatus(), APPROVED);
    }

    @Test
    public void updateMediumStatus() {
        var savedMedium = utilArtworkMetadata.savedMedium(APPROVED);
        var updateRequest = new ArtworkMetadataUpdateRequest(savedMedium.getName(), ArtworkMetadataStatus.PENDING, MEDIUM);
        facade.updateArtworkMetadataStatus(updateRequest, savedMedium.getCreatorId());

        var updated = mediumRepository.findByNameIgnoreCase(savedMedium.getName()).orElseThrow();
        assertEquals(updated.getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteMedium() {
        var savedMedium = utilArtworkMetadata.savedMedium(APPROVED);
        facade.deleteArtworkMetadata(savedMedium.getName(), MEDIUM);

        assertTrue(mediumRepository.findByNameIgnoreCase(savedMedium.getName()).isEmpty());
    }

    @Test
    public void fetchMediums() {
        assertThat(
                facade.searchArtworkMetadata(MEDIUM, null, "", 0, 10, "name").getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedMediumDto();
        assertThat(
                facade.searchArtworkMetadata(MEDIUM, null, "", 0, 10, "name").getEntries(),
                hasItems(dto));
    }

    @Test
    public void createStyle() {
        var admin = utilUser.savedAdmin();
        var name = genUUID();
        var request = new ArtworkMetadataCreateRequest(name, STYLE);
        facade.createArtworkMetadata(request, admin.getId());

        var newlyCreated = styleRepository.findByNameIgnoreCase(name).orElseThrow();
        assertEquals(newlyCreated.getName(), name);
        assertEquals(newlyCreated.getStatus(), APPROVED);
    }

    @Test
    public void updateStyleStatus() {
        var savedStyle = utilArtworkMetadata.savedStyle(APPROVED);
        var updateRequest = new ArtworkMetadataUpdateRequest(savedStyle.getName(), ArtworkMetadataStatus.PENDING, STYLE);
        facade.updateArtworkMetadataStatus(updateRequest, savedStyle.getCreatorId());

        var updated = styleRepository.findByNameIgnoreCase(savedStyle.getName()).orElseThrow();
        assertEquals(updated.getStatus(), ArtworkMetadataStatus.PENDING);
    }

    @Test
    public void deleteStyle() {
        var savedStyle = utilArtworkMetadata.savedStyle(APPROVED);
        facade.deleteArtworkMetadata(savedStyle.getName(), STYLE);

        assertTrue(materialRepository.findByNameIgnoreCase(savedStyle.getName()).isEmpty());
    }

    @Test
    public void fetchStyles() {
        assertThat(
                facade.searchArtworkMetadata(STYLE, null, "", 0, 10, "name").getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedStyleDto();
        assertThat(
                facade.searchArtworkMetadata(STYLE, null, "", 0, 10, "name").getEntries(),
                hasItems(dto));
    }

        @Test
    public void findApprovedCategories() {
        assertThat(
                facade.searchApprovedArtworkMetadata("", CATEGORY).getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedCategoryDto(APPROVED);
        assertThat(
                facade.searchApprovedArtworkMetadata("", CATEGORY).getEntries(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedCategoryDto(APPROVED);
        assertThat(
                facade.searchApprovedArtworkMetadata("", CATEGORY).getEntries(),
                hasItems(dto, dto2));
    }

    @Test
    public void findApprovedMaterials() {
        assertThat(
                facade.searchApprovedArtworkMetadata("", MATERIAL).getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedMaterialDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", MATERIAL).getEntries(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedMaterialDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", MATERIAL).getEntries(),
                hasItems(dto, dto2));
    }

    @Test
    public void findApprovedMediums() {
        assertThat(
                facade.searchApprovedArtworkMetadata("", MEDIUM).getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedMediumDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", MEDIUM).getEntries(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedMediumDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", MEDIUM).getEntries(),
                hasItems(dto, dto2));
    }

    @Test
    public void findApprovedStyles() {
        assertThat(
                facade.searchApprovedArtworkMetadata("", STYLE).getEntries(),
                hasItems());
        var dto = utilArtworkMetadata.savedStyleDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", STYLE).getEntries(),
                hasItems(dto));
        var dto2 = utilArtworkMetadata.savedStyleDto();
        assertThat(
                facade.searchApprovedArtworkMetadata("", STYLE).getEntries(),
                hasItems(dto, dto2));
    }
}
