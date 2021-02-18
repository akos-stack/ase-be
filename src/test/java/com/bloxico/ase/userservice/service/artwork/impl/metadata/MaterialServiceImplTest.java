package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.MATERIAL;

public class MaterialServiceImplTest extends AbstractArtworkMetadataServiceImplTest {

    @Autowired
    private MaterialServiceImpl service;

    @Override
    ArtworkMetadata.Type getType() {
        return MATERIAL;
    }

    @Override
    IArtworkMetadataService getService() {
        return service;
    }

}
