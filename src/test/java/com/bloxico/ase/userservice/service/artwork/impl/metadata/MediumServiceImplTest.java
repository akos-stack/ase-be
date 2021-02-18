package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.MEDIUM;

public class MediumServiceImplTest extends AbstractArtworkMetadataServiceImplTest {

    @Autowired
    private MediumServiceImpl service;

    @Override
    ArtworkMetadata.Type getType() {
        return MEDIUM;
    }

    @Override
    IArtworkMetadataService getService() {
        return service;
    }

}