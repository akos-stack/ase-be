package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import com.bloxico.ase.userservice.entity.artwork.Style;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StyleRepository extends JpaRepository<Style, Long> {

    Optional<Style> findByNameIgnoreCase(String name);

    @Query("SELECT s FROM Style s WHERE (:status IS NULL or s.status = :status) and (:name IS NULL or s.name like %:name%)")
    Page<Style> fetchStyles(@Param("status") ArtworkMetadataStatus status, @Param("name") String name, Pageable pageable);

    List<Style> findAllByStatusAndNameContains(ArtworkMetadataStatus status, String name);
}
