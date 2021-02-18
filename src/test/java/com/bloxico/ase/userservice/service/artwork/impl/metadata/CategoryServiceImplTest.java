package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.CATEGORY;

public class CategoryServiceImplTest extends AbstractArtworkMetadataServiceImplTest {

    @Autowired
    private CategoryServiceImpl service;

    @Override
    ArtworkMetadata.Type getType() {
        return CATEGORY;
    }

    @Override
    IArtworkMetadataService getService() {
        return service;
    }

}
