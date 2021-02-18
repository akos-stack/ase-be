package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.entity.artwork.metadata.Material;
import com.bloxico.ase.userservice.repository.artwork.metadata.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.MATERIAL;

@Service
public class MaterialServiceImpl extends AbstractArtworkMetadataServiceImpl<Material> {

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        super(materialRepository);
    }

    @Override
    protected Type getType() {
        return MATERIAL;
    }

}
