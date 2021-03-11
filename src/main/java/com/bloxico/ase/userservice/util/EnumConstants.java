package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.web.error.ErrorCodes;

import java.util.*;

import static com.bloxico.ase.userservice.util.Functions.throwingMerger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public enum EnumConstants {

    ERROR_CODE {
        @Override
        protected List<Map<String, String>> constants() {
            return Arrays
                    .stream(ErrorCodes.class.getDeclaredClasses())
                    .map(Class::getEnumConstants)
                    .flatMap(Arrays::stream)
                    .map(ErrorCodes.class::cast)
                    .map(ErrorCodes::asMap)
                    .sorted(comparing(m -> m.get("code")))
                    .collect(toList());
        }
    },

    SUPPORTED_FILE_EXTENSION {
        @Override
        protected List<SupportedFileExtension> constants() {
            return List.of(SupportedFileExtension.values());
        }
    },

    FILE_CATEGORY {
        @Override
        protected List<FileCategory> constants() {
            return List.of(FileCategory.values());
        }
    },

    TOKEN_TYPE {
        @Override
        protected List<Token.Type> constants() {
            return List.of(Token.Type.values());
        }
    },

    PENDING_EVALUATOR_STATUS {
        @Override
        protected List<PendingEvaluator.Status> constants() {
            return List.of(PendingEvaluator.Status.values());
        }
    },

    ARTWORK_METADATA_STATUS {
        @Override
        protected List<ArtworkMetadata.Status> constants() {
            return List.of(ArtworkMetadata.Status.values());
        }
    },

    ARTWORK_METADATA_TYPE {
        @Override
        protected List<ArtworkMetadata.Type> constants() {
            return List.of(ArtworkMetadata.Type.values());
        }
    },

    ARTWORK_GROUP_STATUS {
        @Override
        protected List<ArtworkGroup.Status> constants() {
            return List.of(ArtworkGroup.Status.values());
        }
    };

    protected abstract List<?> constants();

    public static Map<String, Object> asMap() {
        return Arrays
                .stream(EnumConstants.values())
                .map(e -> Map.entry(e.name(), e.constants()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        throwingMerger(),
                        TreeMap::new));
    }

}
