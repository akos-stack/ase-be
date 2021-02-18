package com.bloxico.ase.userservice.repository.artwork.metadata;

import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ArtworkMetadataRepository<T extends ArtworkMetadata> extends JpaRepository<T, Long> {

    Optional<T> findByNameIgnoreCase(String name);

    // @formatter:off
    @Query(value = "SELECT m                                        " +
                   "  FROM #{#entityName} m                         " +
                   " WHERE (:status IS NULL OR m.status = :status)  " +
                   "   AND (:name   IS NULL OR m.name LIKE %:name%) ")
    // @formatter:on
    Page<T> search(@Param("status") Status status,
                   @Param("name") String name,
                   Pageable pageable);

    List<T> findAllByStatusAndNameContains(Status status, String name);

}
