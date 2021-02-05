package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.entity.artwork.Material;
import com.bloxico.ase.userservice.repository.artwork.MaterialRepository;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.MATERIAL;

@Slf4j
@Service
public class MaterialServiceImpl extends AbstractArtworkMetadataServiceImpl<Material>{

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        super(materialRepository);
    }

    @Override
    protected ArtworkMetadataType getType() {
        return MATERIAL;
    }
}
