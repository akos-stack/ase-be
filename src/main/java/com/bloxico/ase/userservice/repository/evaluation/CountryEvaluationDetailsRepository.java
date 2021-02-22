package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import com.bloxico.ase.userservice.proj.RegionCountedProj;
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
            "SELECT new com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj( " +
            "         c1.name AS country,                                                     " +
            "         c1.region.name AS region,                                               " +
            "         ced.pricePerEvaluation AS price_per_evaluation,                         " +
            "         ced.availabilityPercentage AS availability_percentage,                  " +
            "         (SELECT COUNT(*)                                                        " +
            "            FROM Evaluator e                                                     " +
            "            JOIN e.userProfile.location.country c2                               " +
            "           WHERE c2 = c1) AS total_of_evaluators)                                " +
            "  FROM CountryEvaluationDetails ced                                              " +
            "  RIGHT JOIN Country c1                                                          " +
            "    ON ced.countryId = c1.id                                                     " +
            "  WHERE (c1.region.name IN :regions OR :regions IS NULL)                         " +
            "  AND (LOWER(c1.name) LIKE LOWER(CONCAT('%', :search, '%'))                      " +
            "  OR LOWER(c1.region.name) LIKE LOWER(CONCAT('%', :search, '%')))                " ,
           countQuery =
            "SELECT COUNT(*) FROM Country c                                                   " +
            "  WHERE (c.region.name IN :regions OR :regions IS NULL)                          " +
            "  AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))                       " +
            "  OR LOWER(c.region.name) LIKE LOWER(CONCAT('%', :search, '%')))                 " )
    // @formatter:on
    Page<CountryEvaluationDetailsCountedProj> findAllCountryEvaluationDetailsWithEvaluatorsCount(
            String search, Collection<String> regions, Pageable pageable);

    // @formatter:off
    @Query(value =
            "SELECT new com.bloxico.ase.userservice.proj.RegionCountedProj(       " +
                    "         r1.id as id,                                                 " +
                    "         r1.name as name,                                             " +
                    "         (SELECT COUNT(*)                                            " +
                    "            FROM Country c                                           " +
                    "           WHERE c.region = r1) AS number_of_countries,              " +
                    "         (SELECT COUNT(*)                                            " +
                    "            FROM Evaluator e                                         " +
                    "            JOIN e.userProfile.location.country.region r2            " +
                    "           WHERE r2 = r1) AS number_of_evaluators)                   " +
                    "  FROM Region r1                                                     " +
                    "  WHERE LOWER(r1.name) LIKE LOWER(CONCAT('%', :search, '%'))         " ,
            countQuery =
                    "SELECT COUNT(r.id) FROM Region r                                     " +
                            "  WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))          " )
    // @formatter:on
    Page<RegionCountedProj> findAllIncludeCountriesAndEvaluatorsCount(String search, Pageable pageable);

}
