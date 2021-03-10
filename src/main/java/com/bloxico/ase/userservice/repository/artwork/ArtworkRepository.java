package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    Artwork findByGroup_Id(Long id);
}
