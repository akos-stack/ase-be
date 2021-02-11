package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findByNameIgnoreCase(String name);

    @Query(value =
            "select " +
                    "new com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj " +
                    "(c.id, c.name, c.region.name, c.countryEvaluationDetails.pricePerEvaluation, " +
                    "c.countryEvaluationDetails.availabilityPercentage, count(e.id)) " +
                    "from Evaluator e " +
                    "right join e.userProfile.location.country c " +
                    "group by c, c.region.name, c.countryEvaluationDetails.pricePerEvaluation, " +
                    "c.countryEvaluationDetails.availabilityPercentage")
    List<CountryTotalOfEvaluatorsProj> findAllIncludeEvaluatorsCount();

    int countByRegionId(int id);

}
