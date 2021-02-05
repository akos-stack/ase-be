package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_METADATA_TYPE_NOT_FOUND;

@Service
public class ArtworkMetadataFacadeImpl extends AbstractArtworkMetadataFacadeImpl {
    private final IArtworkMetadataService categoryMetadataService;
    private final IArtworkMetadataService materialMetadataService;
    private final IArtworkMetadataService mediumMetadataService;
    private final IArtworkMetadataService styleMetadataService;

    public ArtworkMetadataFacadeImpl(@Qualifier("categoryServiceImpl") IArtworkMetadataService categoryMetadataService,
                                     @Qualifier("materialServiceImpl") IArtworkMetadataService materialMetadataService,
                                     @Qualifier("mediumServiceImpl") IArtworkMetadataService mediumMetadataService,
                                     @Qualifier("styleServiceImpl") IArtworkMetadataService styleMetadataService) {
        this.categoryMetadataService = categoryMetadataService;
        this.materialMetadataService = materialMetadataService;
        this.mediumMetadataService = mediumMetadataService;
        this.styleMetadataService = styleMetadataService;
    }

    @Override
    protected IArtworkMetadataService getService(ArtworkMetadataType type) {
       switch (type) {
           case CATEGORY: return categoryMetadataService;
           case MATERIAL: return materialMetadataService;
           case MEDIUM: return mediumMetadataService;
           case STYLE: return styleMetadataService;
           default: throw ARTWORK_METADATA_TYPE_NOT_FOUND.newException();
       }
    }
}
