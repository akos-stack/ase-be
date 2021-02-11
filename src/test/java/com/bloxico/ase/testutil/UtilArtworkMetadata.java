package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.Category;
import com.bloxico.ase.userservice.entity.artwork.Material;
import com.bloxico.ase.userservice.entity.artwork.Medium;
import com.bloxico.ase.userservice.entity.artwork.Style;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus.APPROVED;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;

@Component
public class UtilArtworkMetadata {

    @Autowired private UtilUser utilUser;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private MediumRepository mediumRepository;
    @Autowired private StyleRepository styleRepository;

    public Category savedCategory() {
        var adminId = utilUser.savedAdmin().getId();
        var category = new Category();
        category.setStatus(APPROVED);
        category.setName(genUUID());
        category.setCreatorId(adminId);
        categoryRepository.saveAndFlush(category);
        return category;
    }

    public ArtworkMetadataDto savedCategoryDto() {
        return MAPPER.toDto(savedCategory());
    }

    public Material savedMaterial() {
        var adminId = utilUser.savedAdmin().getId();
        var material = new Material();
        material.setStatus(APPROVED);
        material.setName(genUUID());
        material.setCreatorId(adminId);
        materialRepository.saveAndFlush(material);
        return material;
    }

    public ArtworkMetadataDto savedMaterialDto() {
        return MAPPER.toDto(savedMaterial());
    }

    public Medium savedMedium() {
        var adminId = utilUser.savedAdmin().getId();
        var medium = new Medium();
        medium.setStatus(APPROVED);
        medium.setName(genUUID());
        medium.setCreatorId(adminId);
        mediumRepository.saveAndFlush(medium);
        return medium;
    }

    public ArtworkMetadataDto savedMediumDto() {
        return MAPPER.toDto(savedMedium());
    }

    public Style savedStyle() {
        var adminId = utilUser.savedAdmin().getId();
        var style = new Style();
        style.setStatus(APPROVED);
        style.setName(genUUID());
        style.setCreatorId(adminId);
        styleRepository.saveAndFlush(style);
        return style;
    }

    public ArtworkMetadataDto savedStyleDto() {
        return MAPPER.toDto(savedStyle());
    }
}
