package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkMetadataStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ArtworkMetadataRepository<T> extends JpaRepository<T, Long> {

    Optional<T> findByNameIgnoreCase(String name);

    @Query(value = "SELECT t FROM #{#entityName} t WHERE (:status IS NULL or t.status = :status) and (:name IS NULL or t.name like %:name%)")
    Page<T> search(@Param("status") ArtworkMetadataStatus status, @Param("name") String name, Pageable pageable);

    List<T> findAllByStatusAndNameContains(ArtworkMetadataStatus status, String name);

}
