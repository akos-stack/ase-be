package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
import com.bloxico.ase.testutil.*;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SaveArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.UpdateArtworkMetadataRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.*;
import static com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status.APPROVED;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ArtworkMetadataFacadeImplTest extends AbstractSpringTest {

    @Autowired private UtilUser utilUser;
    @Autowired private UtilArtworkMetadata utilArtworkMetadata;
    @Autowired private ArtworkMetadataFacadeImpl facade;

    @Test
    @WithMockCustomUser
    public void saveArtworkMetadata() {
        for (var type : Type.values()) {
            var name = genUUID();
            var request = new SaveArtworkMetadataRequest(name, type);
            facade.saveArtworkMetadata(request);
            var metadata = utilArtworkMetadata.findArtworkMetadataDto(type, name);
            assertEquals(name, metadata.getName());
            assertSame(APPROVED, metadata.getStatus());
        }
    }

    @Test
    @WithMockCustomUser
    public void updateArtworkMetadata() {
        for (var type : Type.values()) {
            for (var status : Status.values()) {
                var metadata = utilArtworkMetadata.savedArtworkMetadataDto(type, status);
                var request = new UpdateArtworkMetadataRequest(
                        metadata.getName(),
                        randOtherEnumConst(status),
                        type);
                facade.updateArtworkMetadata(request);
                var updated = utilArtworkMetadata.findArtworkMetadataDto(type, metadata.getName());
                assertEquals(metadata.getId(), updated.getId());
                assertEquals(metadata.getName(), updated.getName());
                assertNotEquals(metadata.getStatus(), updated.getStatus());
                assertSame(status, metadata.getStatus());
            }
        }
    }

    @Test
    public void deleteArtworkMetadata() {
        for (var type : Type.values()) {
            for (var status : Status.values()) {
                var metadata = utilArtworkMetadata.savedArtworkMetadataDto(type, status);
                assertNotNull(utilArtworkMetadata.findArtworkMetadataDto(type, metadata.getName()));
                facade.deleteArtworkMetadata(metadata.getName(), type);
                assertNull(utilArtworkMetadata.findArtworkMetadataDto(type, metadata.getName()));
            }
        }
    }

    @Test
    public void searchArtworkMetadata() {
        for (var type : Type.values()) {
            var metadata1 = utilArtworkMetadata.savedArtworkMetadataDto(type, randEnumConst(Status.class));
            var metadata2 = utilArtworkMetadata.savedArtworkMetadataDto(type, randEnumConst(Status.class));
            assertThat(
                    facade.searchArtworkMetadata(type, null, "", 0, 10, "name").getEntries(),
                    hasItems(metadata1, metadata2));
        }
    }

    @Test
    public void searchApprovedArtworkMetadata() {
        for (var type : Type.values()) {
            var metadata1 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            assertThat(
                    facade.searchApprovedArtworkMetadata("", type).getEntries(),
                    hasItems(metadata1));
            var metadata2 = utilArtworkMetadata.savedArtworkMetadataDto(type, APPROVED);
            assertThat(
                    facade.searchApprovedArtworkMetadata("", type).getEntries(),
                    hasItems(metadata1, metadata2));
            var metadata3 = utilArtworkMetadata.savedArtworkMetadataDto(type, randOtherEnumConst(APPROVED));
            assertThat(
                    facade.searchApprovedArtworkMetadata("", type).getEntries(),
                    allOf(
                            hasItems(metadata1, metadata2),
                            not(hasItems(metadata3))));
        }
    }

}
