package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.artwork.ArrayArtworkMetadataResponse;

public interface IArtworkMetadataFacade {

    ArrayArtworkMetadataResponse fetchApprovedCategories(String name);

    ArrayArtworkMetadataResponse fetchApprovedMaterials(String name);

    ArrayArtworkMetadataResponse fetchApprovedMediums(String name);

    ArrayArtworkMetadataResponse fetchApprovedStyles(String name);
}
