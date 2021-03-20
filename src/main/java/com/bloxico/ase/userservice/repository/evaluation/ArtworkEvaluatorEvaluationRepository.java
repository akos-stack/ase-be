package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.ArtworkEvaluatorEvaluation;
import com.bloxico.ase.userservice.proj.evaluation.EvaluatedArtworkProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ArtworkEvaluatorEvaluationRepository extends JpaRepository<ArtworkEvaluatorEvaluation, Long> {

    String EVALUATOR_ID =
            // @formatter:off
            "(SELECT e.id                     " +
            " FROM Evaluator e                " +
            " INNER JOIN e.userProfile up     " +
            " WHERE up.userId = :principalId) ";
            // @formatter:on;

    @Query(value =
            "SELECT new com.bloxico.ase.userservice.proj.evaluation.EvaluatedArtworkProj( " +
            "         a.title as artwork_title,                                           " +
            "         a.artist.name as artist,                                            " +
            "         ae.sellingPrice as selling_price)                                   " +
            "  FROM Artwork a                                                             " +
            "  INNER JOIN ArtworkEvaluatorEvaluation ae ON ae.artworkId = a.id            " +
            "  INNER JOIN a.categories c                                                  " +
            "  WHERE (:artworkTitle IS NULL OR a.title LIKE %:artworkTitle%)              " +
            "  AND (:categories IS NULL OR c.name IN :categories)                         " +
            "  AND  (:principalId IS NULL OR ae.evaluatorId = " + EVALUATOR_ID + ")       ")
    // @formatter:on
    Page<EvaluatedArtworkProj> search(
            String artworkTitle,
            Collection<String> categories,
            Long principalId,
            Pageable pageable);

}
