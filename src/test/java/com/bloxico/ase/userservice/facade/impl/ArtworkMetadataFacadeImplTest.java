package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.MockUtil;
import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArtworkMetadataFacadeImplTest extends AbstractSpringTest {

    @Autowired
    private MockUtil mockUtil;

    @Autowired
    private ArtworkMetadataFacadeImpl facade;

    @Test
    public void findAllCategories() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.fetchApprovedCategories(""));
        var dto = mockUtil.savedCategoryDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.fetchApprovedCategories(""));
        var dto2 = mockUtil.savedCategoryDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.fetchApprovedCategories(""));
    }

    @Test
    public void findAllMaterials() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.fetchApprovedMaterials(""));
        var dto = mockUtil.savedMaterialDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.fetchApprovedMaterials(""));
        var dto2 = mockUtil.savedMaterialDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.fetchApprovedMaterials(""));
    }

    @Test
    public void findAllMediums() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.fetchApprovedMediums(""));
        var dto = mockUtil.savedMediumDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.fetchApprovedMediums(""));
        var dto2 = mockUtil.savedMediumDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.fetchApprovedMediums(""));
    }

    @Test
    public void findAllStyles() {
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of()),
                facade.fetchApprovedStyles(""));
        var dto = mockUtil.savedStyleDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto)),
                facade.fetchApprovedStyles(""));
        var dto2 = mockUtil.savedStyleDto();
        assertEquals(
                new ArrayArtworkMetadataResponse(List.of(dto, dto2)),
                facade.fetchApprovedStyles(""));
    }
}
