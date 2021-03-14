package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.artwork.Artwork.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    // @formatter:off
    @Query(value =
            "SELECT a                                          " +
            "  FROM Artwork a                                  " +
            " WHERE (:status IS NULL OR a.status = :status)    " +
            "   AND (:title  IS NULL OR a.title LIKE %:title%) " +
            "   AND (:owner  IS NULL OR a.ownerId = :owner)    ")
        // @formatter:on
    Page<Artwork> search(@Param("status") Status status,
                         @Param("title") String title,
                         @Param("owner") Long owner,
                         Pageable pageable);

}
