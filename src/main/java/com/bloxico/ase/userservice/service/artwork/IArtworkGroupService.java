package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;

public interface IArtworkGroupService {

    ArtworkGroupDto findGroupById(Long id);

    ArtworkGroupDto findOrUpdateGroup(ArtworkGroupDto dto, long principalId);

    ArtworkGroupDto saveGroup(ArtworkGroupDto dto, long principalId);
}
