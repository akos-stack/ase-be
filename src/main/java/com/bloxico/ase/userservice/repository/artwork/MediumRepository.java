package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.entity.artwork.Medium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediumRepository extends JpaRepository<Medium, Long> {

    Optional<Medium> findByNameIgnoreCase(String name);

    @Query("SELECT m FROM Medium m WHERE (:status IS NULL or m.status = :status) and (:name IS NULL or m.name like %:name%)")
    Page<Medium> fetchMediums(@Param("status") ArtworkMetadataStatus status, @Param("name") String name, Pageable pageable);

    List<Medium> findAllByStatusAndNameContains(ArtworkMetadataStatus status, String name);
}
