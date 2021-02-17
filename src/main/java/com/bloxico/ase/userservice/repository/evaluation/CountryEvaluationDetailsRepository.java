package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryEvaluationDetailsRepository extends JpaRepository<CountryEvaluationDetails, Integer> {

    Optional<CountryEvaluationDetails> findByCountryId(Integer id);

    // @formatter:off
    @Query(value = "SELECT new com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj( " +
                   "         l1.country.name,                                                        " +
                   "         l1.country.region.name,                                                 " +
                   "         ced.pricePerEvaluation,                                                 " +
                   "         ced.availabilityPercentage,                                             " +
                   "         (SELECT COUNT(*)                                                        " +
                   "            FROM Evaluator e                                                     " +
                   "            JOIN e.userProfile.location l2                                       " +
                   "           WHERE l2.country = l1.country))                                       " +
                   "  FROM CountryEvaluationDetails ced                                              " +
                   "  JOIN Location l1                                                               " +
                   "    ON ced.countryId = l1.country.id                                             ")
    // @formatter:on
    List<CountryEvaluationDetailsCountedProj> findAllCountryEvaluationDetailsWithEvaluatorsCount();

}
