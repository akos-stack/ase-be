package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.entity.artwork.*;

public enum ArtworkMetadataType {
    CATEGORY {
        @Override
        public IArtworkMetadataEntity getNewObject() {
            return new Category();
        }
    },

    MATERIAL {
        @Override
        public IArtworkMetadataEntity getNewObject() {
            return new Material();
        }
    },

    MEDIUM {
        @Override
        public IArtworkMetadataEntity getNewObject() {
            return new Medium();
        }
    },

    STYLE {
        @Override
        public IArtworkMetadataEntity getNewObject() {
            return new Style();
        }
    };

    public abstract IArtworkMetadataEntity getNewObject();
}
