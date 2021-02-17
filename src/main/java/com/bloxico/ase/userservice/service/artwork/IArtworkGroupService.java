package com.bloxico.ase.userservice.service.artwork;

import com.bloxico.ase.userservice.dto.entity.artwork.ArtworkGroupDto;

public interface IArtworkGroupService {

    ArtworkGroupDto findGroupById(Long id);

    ArtworkGroupDto saveGroup(long principalId);
}
