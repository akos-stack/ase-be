package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.proj.RegionCountedProj;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedTransferProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CountryEvaluationDetailsRepository extends JpaRepository<CountryEvaluationDetails, Integer> {

    Optional<CountryEvaluationDetails> findByCountryId(Integer id);

    // @formatter:off
    @Query(value =
            "SELECT DISTINCT new com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedTransferProj( " +
            "         c1,                                                                                      " +
            "         c1.id,                                                                                   " +
            "         c1.name as country,                                                                      " +
            "         ced.id,                                                                                  " +
            "         ced.pricePerEvaluation AS price_per_evaluation,                                          " +
            "         ced.availabilityPercentage AS availability_percentage,                                   " +
            "         (SELECT COUNT(*)                                                                         " +
            "            FROM Evaluator e                                                                      " +
            "            JOIN e.userProfile.location.country c2                                                " +
            "           WHERE c2 = c1) AS total_of_evaluators)                                                 " +
            "  FROM CountryEvaluationDetails ced                                                               " +
            "  RIGHT JOIN Country c1 ON ced.countryId = c1.id                                                  " +
            "  JOIN c1.regions r                                                                               " +
            "  WHERE (r.name IN :regions OR :regions IS NULL)                                                  " +
            "  AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))                                        " +
            "  OR LOWER(c1.name) LIKE LOWER(CONCAT('%', :search, '%')))                                        " +
            "  AND (ced IS NOT NULL OR :includeCountriesWithoutEvaluationDetails = TRUE)                       " ,
           countQuery =
            "SELECT COUNT(DISTINCT c)                                                                          " +
            "  FROM CountryEvaluationDetails ced                                                               " +
            "  RIGHT JOIN Country c ON ced.countryId = c.id                                                    " +
            "  JOIN c.regions r                                                                                " +
            "  WHERE (r.name IN :regions OR :regions IS NULL)                                                  " +
            "  AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))                                        " +
            "  OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))                                         " +
            "  AND (ced IS NOT NULL OR :includeCountriesWithoutEvaluationDetails = TRUE)                       " )
    // @formatter:on
    Page<CountryEvaluationDetailsCountedTransferProj> findAllCountryEvaluationDetailsWithEvaluatorsCount(
            String search, Collection<String> regions, boolean includeCountriesWithoutEvaluationDetails, Pageable pageable);

    // @formatter:off
    @Query(value =
            "SELECT new com.bloxico.ase.userservice.proj.RegionCountedProj(       " +
            "         r1.id as id,                                                " +
            "         r1.name as name,                                            " +
            "         (SELECT COUNT(*)                                            " +
            "            FROM Country c                                           " +
            "            JOIN c.regions r2                                        " +
            "           WHERE r2 = r1) AS number_of_countries,                    " +
            "         (SELECT COUNT(*)                                            " +
            "            FROM Evaluator e                                         " +
            "            JOIN e.userProfile.location.country.regions r3           " +
            "           WHERE r3 = r1) AS number_of_evaluators)                   " +
            "  FROM Region r1                                                     " +
            "  WHERE LOWER(r1.name) LIKE LOWER(CONCAT('%', :search, '%'))         " ,
            countQuery =
            "SELECT COUNT(r.id) FROM Region r                                     " +
            "  WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))          " )
    // @formatter:on
    Page<RegionCountedProj> findAllIncludeCountriesAndEvaluatorsCount(String search, Pageable pageable);

}
