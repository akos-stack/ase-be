package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.entity.artwork.metadata.Style;
import com.bloxico.ase.userservice.repository.artwork.metadata.StyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type.STYLE;

@Service
public class StyleServiceImpl extends AbstractArtworkMetadataServiceImpl<Style> {

    @Autowired
    public StyleServiceImpl(StyleRepository styleRepository) {
        super(styleRepository);
    }

    @Override
    protected Type getType() {
        return STYLE;
    }

}
