package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.entity.artwork.metadata.Medium;
import com.bloxico.ase.userservice.repository.artwork.metadata.MediumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.MEDIUM;

@Service
public class MediumServiceImpl extends AbstractArtworkMetadataServiceImpl<Medium> {

    @Autowired
    public MediumServiceImpl(MediumRepository mediumRepository) {
        super(mediumRepository);
    }

    @Override
    protected Type getType() {
        return MEDIUM;
    }

}
