package com.bloxico.ase.userservice.repository.artwork;

import com.bloxico.ase.userservice.entity.artwork.ArtworkDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkDocumentRepository extends JpaRepository<ArtworkDocument, ArtworkDocument.Id> {

    List<ArtworkDocument> findAllById_ArtworkId(Long artworkId);
}
