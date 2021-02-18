package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.entity.artwork.metadata.Category;
import com.bloxico.ase.userservice.repository.artwork.metadata.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.CATEGORY;

@Service
public class CategoryServiceImpl extends AbstractArtworkMetadataServiceImpl<Category> {

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    @Override
    protected Type getType() {
        return CATEGORY;
    }

}
