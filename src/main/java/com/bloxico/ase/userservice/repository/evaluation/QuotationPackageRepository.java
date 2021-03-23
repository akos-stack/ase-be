package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.QuotationPackage;
import com.bloxico.ase.userservice.proj.evaluation.EvaluableArtworkProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationPackageRepository extends JpaRepository<QuotationPackage, Long> {

    Optional<QuotationPackage> findByArtworkId(Long artworkId);

    // @formatter:off
    @Query(value =
            "SELECT DISTINCT new com.bloxico.ase.userservice.proj.evaluation.EvaluableArtworkProj( " +
            "         qp.artworkId            AS artwork_id,                                       " +
            "         a.title                 AS artwork_title,                                    " +
            "         qpc.numberOfEvaluations AS evaluations_limit,                                " +
            "         (SELECT COUNT(*)                                                             " +
            "            FROM ArtworkEvaluatorEvaluation aee                                       " +
            "           WHERE aee.countryId = :countryId                                           " +
            "             AND aee.artworkId = a.id) AS evaluations_taken)                          " +
            "  FROM QuotationPackage qp                                                            " +
            "  JOIN QuotationPackageCountry qpc                                                    " +
            "    ON qp.id = qpc.id.quotationPackageId                                              " +
            "  JOIN Artwork a                                                                      " +
            "    ON a.id = qp.artworkId                                                            " +
            "  JOIN a.categories ac                                                                " +
            " WHERE a.status = 'WAITING_FOR_EVALUATION'                                            " +
            "   AND (:title IS NULL OR a.title like %:title%)                                      " +
            "   AND qpc.id.countryId = :countryId                                                  " +
            "   AND (COALESCE(:categories, NULL) IS NULL OR ac.name IN (:categories))              ",
            countQuery =
            "SELECT COUNT(DISTINCT a.id)                                              " +
            "  FROM QuotationPackage qp                                               " +
            "  JOIN QuotationPackageCountry qpc                                       " +
            "    ON qp.id = qpc.id.quotationPackageId                                 " +
            "  JOIN Artwork a                                                         " +
            "    ON a.id = qp.artworkId                                               " +
            "  JOIN a.categories ac                                                   " +
            " WHERE a.status = 'WAITING_FOR_EVALUATION'                               " +
            "   AND (:title IS NULL OR a.title LIKE %:title%)                         " +
            "   AND qpc.id.countryId = :countryId                                     " +
            "   AND (COALESCE(:categories, NULL) IS NULL OR ac.name IN (:categories)) ")
    // @formatter:on
    Page<EvaluableArtworkProj> searchEvaluableArtworks(Long countryId,
                                                       String title,
                                                       List<String> categories,
                                                       Pageable pageable);

}
