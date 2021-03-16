package com.bloxico.ase.userservice.repository.document;

import com.bloxico.ase.userservice.entity.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // @formatter:off
    @Query("SELECT d                       " +
           "  FROM Document d              " +
           "  JOIN ArtworkDocument ad      " +
           "    ON ad.id.documentId = d.id " +
           " WHERE ad.id.artworkId = :id   ")
    // @formatter:on
    List<Document> findAllByArtworkId(@Param("id") long id);

}
