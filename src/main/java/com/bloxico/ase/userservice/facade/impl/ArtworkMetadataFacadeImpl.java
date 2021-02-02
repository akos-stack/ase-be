package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ArtworkMetadataFacadeImpl implements IArtworkMetadataFacade {

    private final IArtworkMetadataService artworkMetadataService;

    public ArtworkMetadataFacadeImpl(IArtworkMetadataService artworkMetadataService) {
        this.artworkMetadataService = artworkMetadataService;
    }

    @Override
    public ArrayArtworkMetadataResponse fetchApprovedCategories() {
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedCategories - start");
        var artworkMetadataDtos = artworkMetadataService.fetchApprovedCategories();
        var response = new ArrayArtworkMetadataResponse(artworkMetadataDtos);
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedCategories - end");
        return response;
    }

    @Override
    public ArrayArtworkMetadataResponse fetchApprovedMaterials() {
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedMaterials - start");
        var artworkMetadataDtos = artworkMetadataService.fetchApprovedMaterials();
        var response = new ArrayArtworkMetadataResponse(artworkMetadataDtos);
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedMaterials - end");
        return response;
    }

    @Override
    public ArrayArtworkMetadataResponse fetchApprovedMediums() {
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedMediums - start");
        var artworkMetadataDtos = artworkMetadataService.fetchApprovedMediums();
        var response = new ArrayArtworkMetadataResponse(artworkMetadataDtos);
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedMediums - end");
        return response;
    }

    @Override
    public ArrayArtworkMetadataResponse fetchApprovedStyles() {
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedStyles - start");
        var artworkMetadataDtos = artworkMetadataService.fetchApprovedStyles();
        var response = new ArrayArtworkMetadataResponse(artworkMetadataDtos);
        log.info("ArtworkMetadataFacadeImpl.fetchApprovedStyles - end");
        return response;
    }
}
