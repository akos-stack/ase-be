package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.dto.projection.CountryEvaluatorsCounterDto;
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
                    "new com.bloxico.ase.userservice.dto.projection.CountryEvaluatorsCounterDto(c, count(e.id)) " +
                    "from Evaluator e " +
                    "right join e.userProfile.location.city.country c " +
                    "group by c ")
    List<CountryEvaluatorsCounterDto> findAllIncludeEvaluatorsCount();

}
