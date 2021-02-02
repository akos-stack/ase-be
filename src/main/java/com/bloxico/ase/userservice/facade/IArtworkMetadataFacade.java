package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;

public interface IArtworkMetadataFacade {

    ArrayArtworkMetadataResponse fetchApprovedCategories();

    ArrayArtworkMetadataResponse fetchApprovedMaterials();

    ArrayArtworkMetadataResponse fetchApprovedMediums();

    ArrayArtworkMetadataResponse fetchApprovedStyles();
}
