package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.ArtworkEvaluatorEvaluation;
import com.bloxico.ase.userservice.proj.evaluation.EvaluatedArtworkProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ArtworkEvaluatorEvaluationRepository extends JpaRepository<ArtworkEvaluatorEvaluation, Long> {

    // @formatter:off
    @Query(value =
            "SELECT new com.bloxico.ase.userservice.proj.evaluation.EvaluatedArtworkProj( " +
            "         a.title AS artwork_title,                                           " +
            "         a.artist.name AS artist,                                            " +
            "         aee.sellingPrice AS selling_price)                                  " +
            "  FROM Artwork a                                                             " +
            "  JOIN a.categories c                                                        " +
            "  JOIN ArtworkEvaluatorEvaluation aee ON aee.artworkId = a.id                " +
            "  JOIN Evaluator e ON e.id = aee.evaluatorId                                 " +
            "  WHERE (:principalId IS NULL OR e.userProfile.userId = :principalId)        " +
            "  AND (a.title LIKE %:artworkTitle%)                                         " +
            "  AND (COALESCE(:categories, NULL) IS NULL OR c.name IN (:categories))       ")
    // @formatter:on
    Page<EvaluatedArtworkProj> search(
            String artworkTitle,
            Collection<String> categories,
            Long principalId,
            Pageable pageable);

}
