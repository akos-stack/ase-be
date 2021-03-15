package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    String OWNERSHIP_CHECK =
            // @formatter:off
            "(:owner IS NULL                         " +
            " OR a.ownerId = :owner                  " +
            " OR a.status = 'WAITING_FOR_EVALUATION')";
            // @formatter:on;

    // @formatter:off
    @Query(value =
            "SELECT a                                          " +
            "  FROM Artwork a                                  " +
            " WHERE (:title  IS NULL OR a.title LIKE %:title%) " +
            "   AND (:status IS NULL OR a.status = :status)    " +
            "   AND " + OWNERSHIP_CHECK)
    // @formatter:on
    Page<Artwork> search(@Param("status") Status status,
                         @Param("title") String title,
                         @Param("owner") Long owner,
                         Pageable pageable);

    // @formatter:off
    @Query(value =
            "SELECT a          " +
            "  FROM Artwork a  " +
            " WHERE a.id = :id " +
            "   AND " + OWNERSHIP_CHECK)
    // @formatter:on
    Optional<Artwork> findByIdForOwner(@Param("id") Long id,
                                       @Param("owner") Long owner);

}
