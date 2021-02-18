package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.STYLE;

public class StyleServiceImplTest extends AbstractArtworkMetadataServiceImplTest {

    @Autowired
    private StyleServiceImpl service;

    @Override
    ArtworkMetadata.Type getType() {
        return STYLE;
    }

    @Override
    IArtworkMetadataService getService() {
        return service;
    }

}
