package com.bloxico.ase.userservice.service.artwork.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;
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
    public ArtworkGroupDto findOrUpdateGroup(ArtworkGroupDto dto) {
        log.info("ArtworkGroupServiceImpl.findOrUpdateGroup - start | dto: {}", dto);
        requireNonNull(dto);
        requireNonNull(dto.getId());
        var group = artworkGroupRepository
                .findById(dto.getId())
                .orElseThrow(ARTWORK_GROUP_NOT_FOUND::newException);
        if(group.getStatus() != dto.getStatus()) {
            group.setStatus(dto.getStatus());
            group = artworkGroupRepository.saveAndFlush(group);
        }
        log.info("ArtworkGroupServiceImpl.findOrUpdateGroup - end | dto: {}", dto);
        return MAPPER.toDto(group);
    }

    @Override
    public ArtworkGroupDto saveGroup(ArtworkGroupDto dto) {
        log.info("ArtworkGroupServiceImpl.saveGroup - start | dto: {} ", dto);
        requireNonNull(dto);
        var artworkGroup = MAPPER.toEntity(dto);
        log.info("ArtworkGroupServiceImpl.saveGroup - end | dto: {} ", dto);
        return MAPPER.toDto(artworkGroupRepository.save(artworkGroup));
    }
}
