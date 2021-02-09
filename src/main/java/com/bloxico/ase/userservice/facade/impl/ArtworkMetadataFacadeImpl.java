package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.service.artwork.impl.CategoryServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.MaterialServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.MediumServiceImpl;
import com.bloxico.ase.userservice.service.artwork.impl.StyleServiceImpl;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_METADATA_TYPE_NOT_FOUND;

@Service
public class ArtworkMetadataFacadeImpl extends AbstractArtworkMetadataFacadeImpl {
    private final CategoryServiceImpl categoryMetadataService;
    private final MaterialServiceImpl materialMetadataService;
    private final MediumServiceImpl mediumMetadataService;
    private final StyleServiceImpl styleMetadataService;

    public ArtworkMetadataFacadeImpl(CategoryServiceImpl categoryMetadataService,
                                     MaterialServiceImpl materialMetadataService,
                                     MediumServiceImpl mediumMetadataService,
                                     StyleServiceImpl styleMetadataService) {
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
