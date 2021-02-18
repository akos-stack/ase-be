package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.repository.artwork.metadata.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.Util.randEnumConst;
import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

@Component
public class UtilArtworkMetadata {

    @Autowired private UtilUser utilUser;

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private MediumRepository mediumRepository;
    @Autowired private StyleRepository styleRepository;

    @SuppressWarnings("unchecked")
    public <T extends ArtworkMetadata> ArtworkMetadataRepository<T> getRepository(Type type) {
        switch (type) {
            // @formatter:off
            case CATEGORY : return (ArtworkMetadataRepository<T>) categoryRepository;
            case MATERIAL : return (ArtworkMetadataRepository<T>) materialRepository;
            case MEDIUM   : return (ArtworkMetadataRepository<T>) mediumRepository;
            case STYLE    : return (ArtworkMetadataRepository<T>) styleRepository;
            default       : throw new AssertionError();
                // @formatter:on
        }
    }

    public ArtworkMetadata genArtworkMetadata(Type type, Status status) {
        var metadata = type.newInstance();
        metadata.setStatus(status);
        metadata.setName(genUUID());
        return metadata;
    }

    public ArtworkMetadataDto genArtworkMetadataDto(Type type, Status status) {
        return MAPPER.toDto(genArtworkMetadata(type, status));
    }

    public ArtworkMetadata savedArtworkMetadata(Type type, Status status) {
        var principalId = utilUser.savedAdmin().getId();
        var metadata = genArtworkMetadata(type, status);
        metadata.setCreatorId(principalId);
        return getRepository(type).saveAndFlush(metadata);
    }

    public ArtworkMetadataDto savedArtworkMetadataDto(Type type) {
        return savedArtworkMetadataDto(type, randEnumConst(Status.class));
    }

    public ArtworkMetadataDto savedArtworkMetadataDto(Type type, Status status) {
        return MAPPER.toDto(savedArtworkMetadata(type, status));
    }

    public ArtworkMetadataDto findArtworkMetadataDto(Type type, String name) {
        var metadata = getRepository(type)
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .filter(m -> m.getName().equals(name))
                .collect(toList());
        assertTrue(metadata.size() < 2);
        return metadata.isEmpty() ? null : metadata.get(0);
    }

    public List<ArtworkMetadataDto> findAllArtworkMetadataDto(Type type) {
        return getRepository(type)
                .findAll()
                .stream()
                .map(MAPPER::toDto)
                .collect(toList());
    }

}
