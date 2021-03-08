package com.bloxico.ase.userservice.service.artwork.impl.metadata;

import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.PENDING;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractArtworkMetadataServiceImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;

    abstract ArtworkMetadata.Type getType();

    abstract IArtworkMetadataService getService();

    @Test
    public void findOrSaveArtworkMetadata_nullMetadata() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> getService().findOrSaveArtworkMetadata(null, principalId));
    }

    @Test
    public void findOrSaveArtworkMetadata_saved() {
        var principalId = utilUser.savedAdmin().getId();
        for (var status : Status.values()) {
            var metadata = utilArtworkMetadata.genArtworkMetadataDto(getType(), status);
            assertThat(
                    utilArtworkMetadata.findAllArtworkMetadataDto(getType()),
                    not(hasItems(metadata)));
            getService().findOrSaveArtworkMetadata(metadata, principalId);
            assertThat(
                    utilArtworkMetadata.findAllArtworkMetadataDto(getType()),
                    hasItems(metadata));
        }
    }

    @Test
    public void findOrSaveArtworkMetadata_found() {
        var principalId = utilUser.savedAdmin().getId();
        for (var status : Status.values()) {
            var metadata = utilArtworkMetadata.savedArtworkMetadataDto(getType(), status);
            assertThat(
                    utilArtworkMetadata.findAllArtworkMetadataDto(getType()),
                    hasItems(metadata));
            getService().findOrSaveArtworkMetadata(metadata, principalId);
            assertEquals(
                    List.of(metadata.getName()),
                    utilArtworkMetadata
                            .findAllArtworkMetadataDto(getType())
                            .stream()
                            .map(ArtworkMetadataDto::getName)
                            .filter(metadata.getName()::equals)
                            .collect(toList()));
        }
    }

    @Test
    public void updateArtworkMetadata_nullMetadata() {
        var principalId = utilUser.savedAdmin().getId();
        assertThrows(
                NullPointerException.class,
                () -> getService().updateArtworkMetadata(null, principalId));
    }

    @Test
    public void updateArtworkMetadata_notFound() {
        utilArtworkMetadata.savedArtworkMetadataDto(getType(), APPROVED);
        utilArtworkMetadata.savedArtworkMetadataDto(getType(), APPROVED);
        assertNull(utilArtworkMetadata.findArtworkMetadataDto(getType(), PENDING));
    }

    @Test
    public void updateArtworkMetadata() {
        var principalId = utilUser.savedAdmin().getId();
        for (var status : Status.values()) {
            var metadata = utilArtworkMetadata.savedArtworkMetadataDto(getType(), status);
            assertSame(
                    status,
                    utilArtworkMetadata.findArtworkMetadataDto(getType(), metadata.getName()).getStatus());
            metadata.setStatus(randOtherEnumConst(status));
            getService().updateArtworkMetadata(metadata, principalId);
            assertNotSame(
                    status,
                    utilArtworkMetadata.findArtworkMetadataDto(getType(), metadata.getName()).getStatus());
        }
    }

    @Test
    public void deleteCategory_nullName() {
        assertThrows(
                NullPointerException.class,
                () -> getService().deleteArtworkMetadata(null));
    }

    @Test
    public void deleteCategory_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> getService().deleteArtworkMetadata(genUUID()));
    }

    @Test
    public void deleteCategory() {
        for (var status : Status.values()) {
            var metadata = utilArtworkMetadata.savedArtworkMetadataDto(getType(), status);
            assertNotNull(utilArtworkMetadata.findArtworkMetadataDto(getType(), metadata.getName()));
            getService().deleteArtworkMetadata(metadata.getName());
            assertNull(utilArtworkMetadata.findArtworkMetadataDto(getType(), metadata.getName()));
        }
    }

    @Test
    public void searchArtworkMetadata() {
        var metadata1 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), randEnumConst(Status.class));
        assertThat(
                getService().searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(metadata1));
        var metadata2 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), randEnumConst(Status.class));
        assertThat(
                getService().searchArtworkMetadata(null, "", 0, 10, "name").getContent(),
                hasItems(metadata1, metadata2));
    }

    @Test
    public void searchApprovedArtworkMetadata_nullName() {
        var metadata1 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), APPROVED);
        var metadata2 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), PENDING);
        assertThat(
                getService().searchApprovedArtworkMetadata(null),
                not(hasItems(metadata1, metadata2)));
    }

    @Test
    public void searchApprovedArtworkMetadata() {
        var metadata1 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), APPROVED);
        var metadata2 = utilArtworkMetadata.savedArtworkMetadataDto(getType(), PENDING);
        assertThat(
                getService().searchApprovedArtworkMetadata(""),
                allOf(
                        hasItems(metadata1),
                        not(hasItems(metadata2))));
    }

}
