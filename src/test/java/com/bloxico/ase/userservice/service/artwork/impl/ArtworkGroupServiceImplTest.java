package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.securitycontext.WithMockCustomUser;
import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.testutil.UtilArtwork;
import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.exception.ArtworkException;
import com.bloxico.ase.userservice.repository.artwork.ArtworkGroupRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArtworkGroupServiceImplTest extends AbstractSpringTest {

    @Autowired private ArtworkGroupServiceImpl artworkGroupService;
    @Autowired private ArtworkGroupRepository artworkGroupRepository;
    @Autowired private UtilArtwork utilArtwork;

    @Test
    public void findGroupById_nullId() {
        assertThrows(
                NullPointerException.class,
                () -> artworkGroupService.findGroupById(null));
    }

    @Test
    public void findGroupById_notFound() {
        assertThrows(
                ArtworkException.class,
                () -> artworkGroupService.findGroupById(1024L));
    }

    @Test
    public void findGroupById() {
        var artworkGroup= utilArtwork.savedArtworkGroup(ArtworkGroup.Status.WAITING_FOR_EVALUATION);
        var newlyCreated = artworkGroupService.findGroupById(artworkGroup.getId());
        assertNotNull(newlyCreated);
        assertSame(artworkGroup.getStatus(), newlyCreated.getStatus());
    }

    @Test
    @WithMockCustomUser
    public void findOrUpdateGroup_nullGroup() {
        assertThrows(
                NullPointerException.class,
                () -> artworkGroupService.findOrUpdateGroup(null));
    }

    @Test
    @WithMockCustomUser
    public void findOrUpdateGroup_nullGroupId() {
        var dto = new ArtworkGroupDto();
        assertThrows(
                NullPointerException.class,
                () -> artworkGroupService.findOrUpdateGroup(dto));
    }

    @Test
    @WithMockCustomUser
    public void findOrUpdateGroup_groupNotFound() {
        var dto = new ArtworkGroupDto();
        dto.setId(1024L);
        assertThrows(
                ArtworkException.class,
                () -> artworkGroupService.findOrUpdateGroup(dto));
    }

    @Test
    @WithMockCustomUser
    public void findOrUpdateGroup_found() {
        var dto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        var response = artworkGroupService.findOrUpdateGroup(dto);
        assertNotNull(response);
        assertSame(response.getStatus(), dto.getStatus());
    }

    @Test
    @WithMockCustomUser
    public void findOrUpdateGroup_updated() {
        var dto = utilArtwork.savedArtworkGroupDto(ArtworkGroup.Status.DRAFT);
        dto.setStatus(ArtworkGroup.Status.WAITING_FOR_EVALUATION);
        var response = artworkGroupService.findOrUpdateGroup(dto);
        assertNotNull(response);
        assertSame(ArtworkGroup.Status.WAITING_FOR_EVALUATION, response.getStatus());
    }

    @Test
    @WithMockCustomUser
    public void saveGroup_nullGroup() {
        assertThrows(
                NullPointerException.class,
                () -> artworkGroupService.saveGroup(null));
    }

    @Test
    @WithMockCustomUser
    public void saveGroup() {
        var dto = new ArtworkGroupDto();
        dto.setStatus(ArtworkGroup.Status.DRAFT);
        var response = artworkGroupService.saveGroup(dto);
        assertNotNull(response);
        assertTrue(artworkGroupRepository.findById(response.getId()).isPresent());
    }
}
