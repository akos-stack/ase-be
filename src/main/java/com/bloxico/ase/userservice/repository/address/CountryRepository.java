package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.dto.CountryEvaluatorsCounter;
import com.bloxico.ase.userservice.entity.address.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findByNameIgnoreCase(String name);

    @Query(value =
            "select " +
                    "co.id as countryId, count(e.id) as totalOfEvaluators " +
                    "from Evaluator e " +
                    "join e.userProfile up " +
                    "join up.location l " +
                    "join l.city ci " +
                    "right join ci.country co " +
                    "where co.id in :ids " +
                    "group by co.id")
    List<CountryEvaluatorsCounter> countTotalOfEvaluatorsByIdIn(Collection<Integer> ids);

}
