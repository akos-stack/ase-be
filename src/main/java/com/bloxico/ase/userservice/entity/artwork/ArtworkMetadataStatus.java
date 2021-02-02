package com.bloxico.ase.userservice.entity.artwork;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.METADATA_STATUS_NOT_FOUND;

public enum ArtworkMetadataStatus {
    PENDING, APPROVED, DECLINED;

    public static ArtworkMetadataStatus findByName(String name) {
        try {
            return ArtworkMetadataStatus.valueOf(name);
        } catch (Throwable throwable) {
            throw METADATA_STATUS_NOT_FOUND.newException();
        }
    }
}
