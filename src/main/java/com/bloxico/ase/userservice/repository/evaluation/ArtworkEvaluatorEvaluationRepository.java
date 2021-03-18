package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.ArtworkEvaluatorEvaluation;
import com.bloxico.ase.userservice.proj.evaluation.ArtworkEvaluatedProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            "SELECT new com.bloxico.ase.userservice.proj.evaluation.ArtworkEvaluatedProj( " +
            "         a.title as art_name,                                                " +
            "         a.artist.name as artist,                                            " +
            "         ae.sellingPrice as selling_price)                                   " +
            "  FROM Artwork a                                                             " +
            "  INNER JOIN ArtworkEvaluatorEvaluation ae ON ae.artworkId = a.id            " +
            "  INNER JOIN a.categories c                                                  " +
            "  WHERE (:artName IS NULL OR a.title LIKE %:artName%)                        " +
            "  AND (:categories IS NULL OR c.name IN :categories)                         " +
            "  AND  (:principalId IS NULL OR ae.evaluatorId = " + EVALUATOR_ID + ")       ")
    // @formatter:on
    Page<ArtworkEvaluatedProj> search(
            @Param("artName") String artName,
            @Param("categories") Collection<String> categories,
            @Param("principalId") Long principalId,
            Pageable pageable);

}
