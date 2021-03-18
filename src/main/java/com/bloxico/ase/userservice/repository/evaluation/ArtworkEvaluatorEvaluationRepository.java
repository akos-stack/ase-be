package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.ArtworkEvaluatorEvaluation;
import com.bloxico.ase.userservice.proj.evaluation.ArtworkEvaluatedProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkEvaluatorEvaluationRepository extends JpaRepository<ArtworkEvaluatorEvaluation, Long> {

    String OWNERSHIP_CHECK =
    // @formatter:off
            "(:owner IS NULL              " +
            " OR ae.evaluatorId = :owner) ";
    // @formatter:on;

    // @formatter:off
    @Query(value =
            "SELECT new com.bloxico.ase.userservice.proj.evaluation.ArtworkEvaluatedProj( " +
            "         a.title,                                                            " +
            "         a.artist.name,                                                      " +
            "         ae.value,                                                           " +
            "         ae.sellingPrice)                                                    " +
            "  FROM Artwork a                                                             " +
            "  INNER JOIN ArtworkEvaluatorEvaluation ae ON ae.artworkId = a.id            " +
            "  WHERE " + OWNERSHIP_CHECK)
    // @formatter:on
    Page<ArtworkEvaluatedProj> search(
            @Param("owner") Long owner,
            Pageable pageable);

}
