package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.repository.artwork.ArtworkGroupRepository;
import com.bloxico.ase.userservice.service.artwork.IArtworkGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artworks.ARTWORK_GROUP_NOT_FOUND;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ArtworkGroupServiceImpl implements IArtworkGroupService {

    private final ArtworkGroupRepository artworkGroupRepository;

    public ArtworkGroupServiceImpl(ArtworkGroupRepository artworkGroupRepository) {
        this.artworkGroupRepository = artworkGroupRepository;
    }

    @Override
    public ArtworkGroupDto findGroupById(Long id) {
        log.info("ArtworkGroupServiceImpl.findGroupById - start | id: {}", id);
        requireNonNull(id);
        var groupDto = artworkGroupRepository
                .findById(id)
                .map(MAPPER::toDto)
                .orElseThrow(ARTWORK_GROUP_NOT_FOUND::newException);
        log.info("ArtworkGroupServiceImpl.findGroupById - end | id: {}", id);
        return groupDto;
    }

    @Override
    public ArtworkGroupDto saveGroup(long principalId) {
        log.info("ArtworkGroupServiceImpl.saveGroup - start | principalId: {} ", principalId);
        var artworkGroup = new ArtworkGroup();
        artworkGroup.setCreatorId(principalId);
        log.info("ArtworkGroupServiceImpl.saveGroup - end | principalId: {} ", principalId);
        return MAPPER.toDto(artworkGroupRepository.save(artworkGroup));
    }
}
