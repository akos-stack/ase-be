package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.entity.artwork.Style;
import com.bloxico.ase.userservice.repository.artwork.StyleRepository;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.ArtworkMetadataType.STYLE;

@Slf4j
@Service
public class StyleServiceImpl extends AbstractArtworkMetadataServiceImpl<Style>{

    @Autowired
    public StyleServiceImpl(StyleRepository styleRepository) {
        super(styleRepository);
    }

    @Override
    protected ArtworkMetadataType getType() {
        return STYLE;
    }
}
