package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.util.ArtworkMetadataType;

public interface IArtworkMetadataRequest {

    String getName();

    ArtworkMetadataStatus getStatus();

    ArtworkMetadataType getType();
}
