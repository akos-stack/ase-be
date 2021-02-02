package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.dto.CountryIdTotalOfEvaluatorsPair;
import com.bloxico.ase.userservice.entity.user.profile.Evaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EvaluatorRepository extends JpaRepository<Evaluator, Long> {

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
    List<CountryIdTotalOfEvaluatorsPair> countTotalOfEvaluatorsForEachCountryIdIn(Collection<Integer> ids);

}
