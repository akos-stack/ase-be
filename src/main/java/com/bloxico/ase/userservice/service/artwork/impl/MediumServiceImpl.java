package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.entity.artwork.Medium;
import com.bloxico.ase.userservice.repository.artwork.MediumRepository;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.MEDIUM;

@Slf4j
@Service
public class MediumServiceImpl extends AbstractArtworkMetadataServiceImpl<Medium>{

    @Autowired
    public MediumServiceImpl(MediumRepository mediumRepository) {
        super(mediumRepository);
    }

    @Override
    protected ArtworkMetadataType getType() {
        return MEDIUM;
    }
}
