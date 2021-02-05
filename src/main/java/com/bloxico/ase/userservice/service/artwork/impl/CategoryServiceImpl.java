package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.entity.artwork.Category;
import com.bloxico.ase.userservice.repository.artwork.CategoryRepository;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.CATEGORY;

@Slf4j
@Service
public class CategoryServiceImpl extends AbstractArtworkMetadataServiceImpl<Category>{

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    @Override
    protected ArtworkMetadataType getType() {
        return CATEGORY;
    }
}
