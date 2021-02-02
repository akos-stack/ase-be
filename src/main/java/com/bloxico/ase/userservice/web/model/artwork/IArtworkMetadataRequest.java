package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;

public interface IArtworkMetadataRequest {

    String getName();

    ArtworkMetadataStatus getStatus();
}
