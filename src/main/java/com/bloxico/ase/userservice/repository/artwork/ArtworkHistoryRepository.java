package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkHistoryRepository extends JpaRepository<ArtworkHistory, Long> {
}
