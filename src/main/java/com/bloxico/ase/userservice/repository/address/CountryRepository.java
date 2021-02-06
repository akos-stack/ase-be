package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.dto.projection.CountryProjection;
import com.bloxico.ase.userservice.entity.address.Country;
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
                    "new com.bloxico.ase.userservice.dto.projection.CountryProjection " +
                    "(co.id, co.name, " +
                    "r.id, r.name, " +
                    "ced.id, ced.pricePerEvaluation, ced.availabilityPercentage, count(e.id)) " +
                    "from Evaluator e " +
                    "join e.userProfile up " +
                    "join up.location l " +
                    "join l.city ci " +
                    "right join ci.country co " +
                    "join co.countryEvaluationDetails ced " +
                    "join co.region r " +
                    "group by co.id, r.id, ced.id")
    List<CountryProjection> findAllComplete();

}
