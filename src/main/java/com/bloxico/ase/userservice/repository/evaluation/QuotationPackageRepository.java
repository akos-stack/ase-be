package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.QuotationPackage;
import com.bloxico.ase.userservice.proj.evaluation.OngoingEvaluationsProj;
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
            "SELECT DISTINCT new com.bloxico.ase.userservice.proj.evaluation.OngoingEvaluationsProj( " +
                    "  qp.artworkId,                                                        " +
                    "  a.title,                                                             " +
                    "  qpc.numberOfEvaluations,                                             " +
                    "  (SELECT COUNT(*) FROM ArtworkEvaluatorEvaluation aee                 " +
                    " WHERE aee.countryId = :countryId and aee.artworkId = a.id))           " +
                    "  FROM QuotationPackage qp " +
                    "  JOIN QuotationPackageCountry qpc on qp.id = qpc.id.quotationPackageId" +
                    "  JOIN Artwork a on a.id = qp.artworkId                                " +
                    "  JOIN a.categories ac                                                 " +
                    "  WHERE a.status = 'READY_FOR_EVALUATION'                              " +
                    "   and (:title is NULL or a.title like %:title%)                       " +
                    "   and qpc.id.countryId = :countryId                                   " +
                    "   and ((:categories) is NULL or ac.name in (:categories))               ",
            countQuery =
            "SELECT DISTINCT COUNT(a.id) FROM QuotationPackage qp                           " +
                    " JOIN QuotationPackageCountry qpc on qp.id = qpc.id.quotationPackageId " +
                    " JOIN Artwork a on a.id = qp.artworkId                                 " +
                    " JOIN a.categories ac                                                  " +
                    " WHERE a.status = 'READY_FOR_EVALUATION'                               " +
                    "   and (:title is NULL or a.title like %:title%)                       " +
                    "   and qpc.id.countryId = :countryId                                   " +
                    "   and ((:categories) is NULL or ac.name in (:categories))")
    // @formatter:on
    Page<OngoingEvaluationsProj> searchOngoingEvaluations(Long countryId, String title, List<String> categories, Pageable pageable);
}
