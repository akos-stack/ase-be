package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    // @formatter:off
    @Query(value = "SELECT m                                        " +
            "  FROM Artwork m                         " +
            " WHERE (:status IS NULL OR m.status = :status)  " +
            "   AND (:title   IS NULL OR m.title LIKE %:title%) " +
            "   AND (:owner   IS NULL OR m.ownerId = :owner) ")
        // @formatter:on
    Page<Artwork> search(@Param("status") Artwork.Status status,
                   @Param("title") String title, @Param("owner") Long owner,
                   Pageable pageable);
}
