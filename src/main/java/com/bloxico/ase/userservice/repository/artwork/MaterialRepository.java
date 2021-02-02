package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.entity.artwork.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByNameIgnoreCase(String name);

    @Query("SELECT m FROM Material m WHERE (:status IS NULL or m.status = :status) and (:name IS NULL or m.name like %:name%)")
    Page<Material> fetchMaterials(@Param("status") ArtworkMetadataStatus status, @Param("name") String name, Pageable pageable);

    List<Material> findAllByStatus(ArtworkMetadataStatus status);
}
