package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;

public interface IArtworkMetadataRequest {

    String getName();

    Status getStatus();

    Type getType();

}
